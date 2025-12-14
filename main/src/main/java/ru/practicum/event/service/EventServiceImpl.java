package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventSpecification;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.enums.StateActionUser;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.exceptions.AlreadyExistsException;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.participationRequest.dto.UpdateEventAdminRequest;
import ru.practicum.participationRequest.dto.UpdateEventUserRequest;
import ru.practicum.participationRequest.service.RequestService;
import ru.practicum.user.Service.UserService;
import ru.practicum.user.model.User;
import ru.practicum.utils.PaginationUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static ru.practicum.utils.DateTimeConstant.DATE_TIME_PATTERN;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventCommonService eventCommonService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final RequestService requestService;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;
    @Value("${app}")
    String app;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {

        Pageable pageable = PaginationUtils.createPageable(from, size, null);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();

        List<Long> eventsIds = events.stream()
                .map(Event::getId)
                .toList();

        HashMap<Long, Long> confirmedRequests = requestService.getConfirmedRequestsCount(eventsIds);
        HashMap<Long, Long> views = eventCommonService.getViews(events);

        return events.stream()
                .map(event -> {
                    long eventId = event.getId();
                    long viewsCount = views.getOrDefault(eventId, 0L);
                    EventShortDto dto = eventMapper.toShortDto(event);
                    dto.setViews(viewsCount);
                    dto.setConfirmedRequests(confirmedRequests.getOrDefault(eventId, 0L));
                    return dto;
                })
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        if (!isTwoHoursLater(newEventDto.getEventDate())) {
            throw new BadRequestException("Событие должно содержать дату, не раньше 2-х часов после добавления события");
        }

        User initiator = userService.getUserOrThrow(userId);
        Category category = categoryService.getCategoryEntityById(newEventDto.getCategory());
        Location location = locationService.saveLocation(newEventDto.getLocation());

        Event event = eventMapper.toEntity(newEventDto, category, initiator, location);
        event.setState(State.PENDING);

        Event savedEvent = eventRepository.save(event);
        long confirmedRequests = requestService.getCountConfirmedRequestsByEventId(event.getId());
        return eventMapper.toFullDto(savedEvent, confirmedRequests, 0L);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdAndUserId(Long userId, Long eventId) {

        userService.getUserOrThrow(userId);

        Event event = eventCommonService.findByEventIdAndInitiatorIdOrThrow(eventId, userId);
        long confirmedRequests = requestService.getCountConfirmedRequestsByEventId(event.getId());
        Long views = eventCommonService.getViews(List.of(event)).get(event.getId());

        return eventMapper.toFullDto(event, confirmedRequests, views);
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
            if (!isTwoHoursLater(updateRequest.getEventDate())) {
                throw new BadRequestException("Событие должно содержать дату, не раньше 2-х часов после добавления события");
            }
        }

        Category category = null;
        if (updateRequest.getCategory() != null) {
            category = categoryService.getCategoryEntityById(updateRequest.getCategory());
        }

        Location location = null;
        if (updateRequest.getLocation() != null) {
            Long locationId = updateRequest.getLocation().getId();
            if (locationId != null) {
                location = locationService.getLocation(locationId);
            } else {
                location = locationService.saveLocation(updateRequest.getLocation());
            }
        }


        eventMapper.patchUserRequest(event, updateRequest, category, location);

        if (updateRequest.getStateAction() != null) {
            StateActionUser action = Enum.valueOf(StateActionUser.class, updateRequest.getStateAction());
            event.setState(action == StateActionUser.SEND_TO_REVIEW
                    ? State.PENDING
                    : State.CANCELED
            );
        }

        event = eventRepository.save(event);
        Long confirmedRequests = requestService.getCountConfirmedRequestsByEventId(event.getId());
        Long views = eventCommonService.getViews(List.of(event)).get(event.getId());

        return eventMapper.toFullDto(event, confirmedRequests, views);
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


        // Парсим
        LocalDateTime start = this.parseStringToTime(rangeStart, null);
        LocalDateTime end = this.parseStringToTime(rangeEnd, null);
        List<State> stateEnum = (states != null && !states.isEmpty())
                ? states.stream().map(State::valueOf).toList()
                : null;

        Pageable pageable = PaginationUtils.createPageable(from, size, null);

        Specification<Event> specification = Specification.where(EventSpecification.byUsers(users)
                .and(EventSpecification.byState(stateEnum))
                .and(EventSpecification.byCategories(categories))
                .and(EventSpecification.byStartDate(start))
                .and(EventSpecification.byEndDate(end)));

        Page<Event> eventPage = eventRepository.findAll(specification, pageable);
        List<Event> events = eventPage.getContent();
        HashMap<Long, Long> views = eventCommonService.getViews(events);

        return events.stream()
                .map(event -> {
                    long confirmedRequests = requestService.getCountConfirmedRequestsByEventId(event.getId());
                    return eventMapper.toFullDto(event, confirmedRequests, views.get(event.getId()));
                })
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        Event event = eventRepository.findByIdWithLocation(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Category category = event.getCategory();
        if (updateEventAdminRequest.getCategory() != null) {
            category = categoryService.getCategoryEntityById(updateEventAdminRequest.getCategory());
        }

        Location location = event.getLocation();
        if (updateEventAdminRequest.getLocation() != null) {
            if (updateEventAdminRequest.getLocation().getId() != null) {
                location = updateEventAdminRequest.getLocation();
            } else {
                location = locationService.saveLocation(location);
            }
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(),
                    DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("The event start date must be no earlier than an hour after the publication date");
            }
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            StateActionAdmin action = Enum.valueOf(StateActionAdmin.class, updateEventAdminRequest.getStateAction());
            switch (action) {
                case StateActionAdmin.PUBLISH_EVENT -> handlePublishEvent(event);
                case StateActionAdmin.REJECT_EVENT -> handleRejectEvent(event);
            }
        }

        eventMapper.patchAdminRequest(event, updateEventAdminRequest, category, location);

        Long confirmedRequests = requestService.getCountConfirmedRequestsByEventId(event.getId());
        Long view = eventCommonService.getViews(List.of(event)).get(event.getId());
        return eventMapper.toFullDto(event, confirmedRequests, view);
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

        // Парсим;
        LocalDateTime start = parseStringToTime(rangeStart, LocalDateTime.now());
        LocalDateTime end = parseStringToTime(rangeEnd,
                LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        if (start.isAfter(end)) throw new BadRequestException("The start date cannot be after the end date.");
        if (end.isBefore(start)) throw new BadRequestException("The end date cannot be earlier than the start date.");

        String sortBy = null;
        if (sort != null) sortBy = sort.equals("EVENT_DATE") ? "eventDate" : "views";

        Pageable pageable = PaginationUtils.createPageable(from, size, sortBy);

        Specification<Event> specification = Specification.where(EventSpecification.isPublished())
                .and(EventSpecification.textContains(text))
                .and(EventSpecification.byCategories(categories))
                .and(EventSpecification.isPaid(paid))
                .and(EventSpecification.byStartDate(start))
                .and(EventSpecification.byEndDate(end))
                .and(EventSpecification.isOnlyAvailable(onlyAvailable));


        List<Event> events = eventRepository.findAll(specification, pageable).getContent();

        List<Long> evenIds = events.stream()
                .map(Event::getId)
                .toList();

        HashMap<Long, Long> confirmedRequests = requestService.getConfirmedRequestsCount(evenIds);
        HashMap<Long, Long> views = eventCommonService.getViews(events);
        postHit(request);

        return events.stream()
                .map(event -> {
                    long eventId = event.getId();
                    long viewsCount = views.getOrDefault(eventId, 0L);
                    EventShortDto dto = eventMapper.toShortDto(event);
                    dto.setConfirmedRequests(confirmedRequests.getOrDefault(eventId, 0L));
                    dto.setViews(viewsCount);
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdPublic(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        long confirmedRequests = requestService.getCountConfirmedRequestsByEventId(event.getId());

        List<ViewStatsDto> statsDto = statsClient.getStats(
                event.getCreatedOn(),
                event.getEventDate(),
                List.of(request.getRequestURI()), true);
        HashMap<Long, Long> views = eventCommonService.getViews(List.of(event));
        postHit(request);
        return eventMapper.toFullDto(event, confirmedRequests, views.get(event.getId()));
    }

    // Вспомогательные методы

    private static boolean isTwoHoursLater(String eventDate) {
        LocalDateTime targetDateTime = LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, targetDateTime);
        return duration.toHours() >= 2;
    }

    private void handlePublishEvent(Event event) {
        State currentState = event.getState();

        if (currentState == State.PUBLISHED || currentState == State.CANCELED) {
            throw new AlreadyExistsException(
                    "Cannot publish the event because it's not in the right state: PUBLISHED");
        }

        if (currentState == State.PENDING) {
            event.setState(State.PUBLISHED);
        }
    }

    private void handleRejectEvent(Event event) {
        State currentState = event.getState();

        if (currentState == State.PUBLISHED || currentState == State.CANCELED) {
            throw new AlreadyExistsException(
                    "Cannot decline event: it has already been published");
        }

        if (currentState == State.PENDING) {
            event.setState(State.CANCELED);
        }
    }

    private LocalDateTime parseStringToTime(String time, LocalDateTime defaultValue) {
        if (time != null && !time.isEmpty()) {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        } else {
            return defaultValue;
        }
    }

    private void postHit(HttpServletRequest request) {
        EndpointHitDto hitDto = new EndpointHitDto(
                app,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());

        statsClient.postHit(hitDto);
    }
}

