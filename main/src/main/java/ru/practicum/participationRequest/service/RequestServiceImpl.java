package ru.practicum.participationRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.impl.EventCommonServiceImpl;
import ru.practicum.exceptions.AlreadyExistsException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.participationRequest.RequestMapper;
import ru.practicum.participationRequest.RequestRepository;
import ru.practicum.participationRequest.dto.EventConfirmedCountDto;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.participationRequest.model.ParticipationRequest;
import ru.practicum.participationRequest.model.RequestStatus;
import ru.practicum.user.Service.UserService;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final UserService userService;
    private final EventCommonServiceImpl eventCommonServiceImpl;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);

        return requests
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        User requester = userService.getUserOrThrow(userId);
        Event event = eventCommonServiceImpl.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " was not found"));

        // Проерка на создание повторного запроса на участие в событии
        if (existsByUserIdAndEventId(userId, eventId)) {
            throw new AlreadyExistsException("It is not possible to add a repeat request for participation in an event.");
        }
        // Проверка что организатор события не пытается добавить себя участником
        if (event.getInitiator().getId().equals(userId)) {
            throw new AlreadyExistsException("You can't register for your event.");
        }
        // Проверка что событие опубликовано
        if (event.getState() != State.PUBLISHED) {
            throw new AlreadyExistsException("You cannot participate in an unpublished event");
        }

        RequestStatus status = determineRequestStatus(event);

        ParticipationRequest request = cteateParticipationRequest(requester, event, status);

        ParticipationRequest savedRequest = requestRepository.save(request);

        return requestMapper.toDto(savedRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request for participation in the event with requestId" + requestId + " not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new AlreadyExistsException("Cannot cancel request of another user");
        }

        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        return requestMapper.toDto(request);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(long userId, long eventId) {

        if (!eventCommonServiceImpl.existsByIdAndInitiatorId(eventId, userId)) {
            throw new AlreadyExistsException("The event does not belong to the user");
        }

        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult patchStatusRequestsByUserIdAndEventId(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest updateRequest) {

        // Проверка наличия события и прав пользователя
        Event event = eventCommonServiceImpl.findByEventIdAndInitiatorIdOrThrow(eventId, userId);

        // Подсчет подтвержденных запросов
        long countConfirmedRequests = getCountConfirmedRequestsByEventId(eventId);
        if (countConfirmedRequests == event.getParticipantLimit()) {
            throw new AlreadyExistsException("The participant limit has been reached");
        }

        // Список запросов переданных для обновления статуса со статусом PENDING
        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdInAndStatus(
                eventId,
                updateRequest.getRequestIds(),
                RequestStatus.PENDING);

        if (requests.isEmpty()) {
            log.info("No pending requests found for eventId={} and requestIds={}", eventId, updateRequest.getRequestIds());
            return new EventRequestStatusUpdateResult(Collections.emptyList(), Collections.emptyList());
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        RequestStatus newStatus = updateRequest.getStatus();
        if (newStatus == RequestStatus.REJECTED ||
                newStatus == RequestStatus.CANCELED) {
            // Обновление статусов
            List<ParticipationRequest> updatedRequests = requests.stream()
                    .map(request -> patchRequestStatus(request, newStatus))
                    .toList();
            // Маппинг в DTO
            rejectedRequests = updatedRequests.stream()
                    .map(requestMapper::toDto)
                    .toList();

        } else if (newStatus == RequestStatus.CONFIRMED) {
            for (ParticipationRequest request : requests) {
                if (countConfirmedRequests < event.getParticipantLimit()) {

                    ParticipationRequest savedRequest = patchRequestStatus(request, newStatus);
                    confirmedRequests.add(requestMapper.toDto(savedRequest));
                    countConfirmedRequests++;
                } else {
                    ParticipationRequest rejectedRequest = patchRequestStatus(request, newStatus);
                    rejectedRequests.add(requestMapper.toDto(rejectedRequest));
                }
            }
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public long getCountConfirmedRequestsByEventId(long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    @Override
    public HashMap<Long, Long> getConfirmedRequestsCount(List<Long> eventIds) {
        List<EventConfirmedCountDto> results = requestRepository.findConfirmedCountsByEventIds(eventIds);

        return results.stream()
                .collect(Collectors.toMap(
                        EventConfirmedCountDto::getEventId,
                        EventConfirmedCountDto::getConfirmedCount,
                        (oldValue, newValue) -> oldValue,
                        HashMap::new
                ));
    }

    // Обновляем статус заявки на участие в событии
    private ParticipationRequest patchRequestStatus(ParticipationRequest request, RequestStatus status) {
        request.setStatus(status);
        return requestRepository.save(request);
    }

    // Создание запроса на участие в событии
    private ParticipationRequest cteateParticipationRequest(User requester, Event event, RequestStatus status) {
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setEvent(event);
        participationRequest.setRequester(requester);
        participationRequest.setStatus(status);
        return participationRequest;
    }

    // Опреляем статус запроса для добавления
    private RequestStatus determineRequestStatus(Event event) {
        if (hasUnlimitedParticipants(event)) {
            return RequestStatus.CONFIRMED;
        }

        if (isParticipantLimitReached(event) && !event.isRequestModeration()) {
            throw new AlreadyExistsException("Reach the participant limit for an event");
        }

        return RequestStatus.PENDING;
    }

    private boolean hasUnlimitedParticipants(Event event) {
        return event.getParticipantLimit() == 0;
    }

    private boolean isParticipantLimitReached(Event event) {
        return countRequestsByEventId(event.getId()) >= event.getParticipantLimit();
    }

    // Получение кол-ва заявок на участие в событии оп id
    private int countRequestsByEventId(long eventId) {
        return requestRepository.countByEventId(eventId);
    }

    // Проверка что запрос уже создан
    private boolean existsByUserIdAndEventId(Long userId, Long eventId) {
        return requestRepository.existsByRequesterIdAndEventId(userId, eventId);
    }

}
