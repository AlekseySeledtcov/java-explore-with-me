package ru.practicum.event.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.*;
import ru.practicum.exceptions.AlreadyExistsException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.participationRequest.dto.UpdateEventAdminRequest;
import ru.practicum.participationRequest.dto.UpdateEventUserRequest;
import ru.practicum.user.Service.UserService;
import ru.practicum.utils.PaginationUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventCommonService eventCommonService;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final EventEnrichmentService eventEnrichmentService;
    private final StatsService statsService;
    private final EventValidationService eventValidationService;
    private final EventStateService eventStateService;
    private final EventSearchService eventSearchService;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = PaginationUtils.createPageable(from, size, null);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        return eventEnrichmentService.enrichmentEventShortDto(events);
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        eventValidationService.assertAtLeastTwoHoursFromNow(newEventDto.getEventDate());

        Event event = eventEnrichmentService.enrichmentEventFromNewEventDto(userId, newEventDto);
        event.setState(State.PENDING);
        Event savedEvent = eventRepository.save(event);

        return eventEnrichmentService.enrichmentEventFullDto(savedEvent);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdAndUserId(Long userId, Long eventId) {
        userService.getUserOrThrow(userId);

        Event event = eventCommonService.findByEventIdAndInitiatorIdOrThrow(eventId, userId);

        return eventEnrichmentService.enrichmentEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByIdAndUserId(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        userService.getUserOrThrow(userId);

        Event event = eventCommonService.findByEventIdAndInitiatorIdOrThrow(eventId, userId);

        if (event.getState() == State.PUBLISHED) {
            throw new AlreadyExistsException("Only pending or canceled events can be changed");
        }

        if (updateRequest.getEventDate() != null && !updateRequest.getEventDate().isBlank()) {
            eventValidationService.assertAtLeastTwoHoursFromNow(updateRequest.getEventDate());
        }

        event = eventEnrichmentService.enrichmentEventFromUpdateEventUserRequest(event, updateRequest);

        if (updateRequest.getStateAction() != null) {
            event.setState(eventStateService.setStateByStateActionUser(updateRequest.getStateAction()));
        }

        event = eventRepository.save(event);

        return eventEnrichmentService.enrichmentEventFullDto(event);
    }

    //Admin
    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsAdmin(List<Long> users,
                                             List<String> states,
                                             List<Long> categories,
                                             String rangeStart,
                                             String rangeEnd,
                                             int from,
                                             int size) {

        Pageable pageable = PaginationUtils.createPageable(from, size, null);
        Specification<Event> specification = eventSearchService.getEventsAdminSpecification(users, states, categories,
                rangeStart, rangeEnd);

        List<Event> events = eventRepository.findAll(specification, pageable).getContent();

        return eventEnrichmentService.enrichmentEventFullDtos(events);
    }

    @Transactional
    @Override
    public EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest updateRequest) {

        Event event = eventRepository.findByIdWithLocation(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updateRequest.getEventDate() != null) {
            eventValidationService.assertAtLeastOneHoursFromNow(updateRequest.getEventDate());
        }

        event = eventEnrichmentService.enrichmentEventFromUpdateEventAdminRequest(event, updateRequest);

        return eventEnrichmentService.enrichmentEventFullDto(event);
    }

    //Public
    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               int from,
                                               int size,
                                               HttpServletRequest request) {

        String sortBy = null;
        if (sort != null) sortBy = sort.equals("EVENT_DATE") ? "eventDate" : "views";

        Pageable pageable = PaginationUtils.createPageable(from, size, sortBy);

        Specification<Event> specification = eventSearchService.getEventsPublicSpecification(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable);

        List<Event> events = eventRepository.findAll(specification, pageable).getContent();

        List<EventShortDto> result = eventEnrichmentService.enrichmentEventShortDto(events);

        statsService.postHit(request);

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdPublic(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        EventFullDto result = eventEnrichmentService.enrichmentEventFullDto(event);

        statsService.postHit(request);

        return result;
    }
}

