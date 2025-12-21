package ru.practicum.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventEnrichmentService;
import ru.practicum.event.service.EventStateService;
import ru.practicum.event.service.StatsService;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.participationRequest.dto.UpdateEventAdminRequest;
import ru.practicum.participationRequest.dto.UpdateEventUserRequest;
import ru.practicum.participationRequest.service.RequestService;
import ru.practicum.user.Service.UserService;
import ru.practicum.user.model.User;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventEnrichmentServiceImpl implements EventEnrichmentService {

    private final RequestService requestService;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final EventStateService eventStateService;
    private final StatsService statsService;

    @Override
    public List<EventShortDto> enrichmentEventShortDto(List<Event> events) {
        List<Long> eventIds = getIdsByEvents(events);
        HashMap<Long, Long> confirmedRequests = getMapCountConfirmedRequestsByEventIds(eventIds);
        HashMap<Long, Long> views = statsService.getMapCountViewByEvents(events);
        return events.stream()
                .map(event -> {
                    long eventId = event.getId();
                    long countConfirmedRequests = confirmedRequests.getOrDefault(eventId, 0L);
                    long countViews = views.getOrDefault(eventId, 0L);
                    EventShortDto dto = eventMapper.toShortDto(event);
                    dto.setConfirmedRequests(countConfirmedRequests);
                    dto.setViews(countViews);
                    return dto;
                })
                .toList();
    }


    @Override
    public Event enrichmentEventFromNewEventDto(Long userId, NewEventDto newEventDto) {
        User initiator = userService.getUserOrThrow(userId);
        Category category = categoryService.getCategoryEntityByIdOrThrow(newEventDto.getCategory());
        Location location = locationService.saveLocation(newEventDto.getLocation());

        return eventMapper.toEntity(newEventDto, category, initiator, location);
    }


    @Override
    public EventFullDto enrichmentEventFullDto(Event event) {
        long countConfirmedRequests = requestService.getCountConfirmedRequestsByEventId(event.getId());
        long countViews = statsService.getCountViewByEvent(event);
        EventFullDto result = eventMapper.toFullDto(event);
        result.setConfirmedRequests(countConfirmedRequests);
        result.setViews(countViews);
        return result;
    }


    @Override
    public Event enrichmentEventFromUpdateEventUserRequest(Event event, UpdateEventUserRequest updateRequest) {
        Category category = null;
        if (updateRequest.getCategory() != null) {
            category = categoryService.getCategoryEntityByIdOrThrow(updateRequest.getCategory());
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
        return event;
    }


    @Override
    public List<EventFullDto> enrichmentEventFullDtos(List<Event> events) {
        List<Long> eventIds = getIdsByEvents(events);
        HashMap<Long, Long> confirmedRequests = getMapCountConfirmedRequestsByEventIds(eventIds);
        HashMap<Long, Long> views = statsService.getMapCountViewByEvents(events);
        return events.stream()
                .map(event -> {
                    long eventId = event.getId();
                    long countConfirmedRequests = confirmedRequests.getOrDefault(eventId, 0L);
                    long countViews = views.getOrDefault(eventId, 0L);
                    EventFullDto dto = eventMapper.toFullDto(event);
                    dto.setConfirmedRequests(countConfirmedRequests);
                    dto.setViews(countViews);
                    return dto;
                })
                .toList();
    }


    @Override
    public Event enrichmentEventFromUpdateEventAdminRequest(Event event, UpdateEventAdminRequest updateRequest) {
        Category category = null;
        if (updateRequest.getCategory() != null) {
            category = categoryService.getCategoryEntityByIdOrThrow(updateRequest.getCategory());
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

        if (updateRequest.getStateAction() != null) {
            event = eventStateService.setStatByStateActionAdmin(event, updateRequest.getStateAction());
        }

        eventMapper.patchAdminRequest(event, updateRequest, category, location);

        return event;
    }


    private HashMap<Long, Long> getMapCountConfirmedRequestsByEventIds(List<Long> eventIds) {
        return requestService.getConfirmedRequestsCount(eventIds);
    }

    private List<Long> getIdsByEvents(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .toList();
    }
}
