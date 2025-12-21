package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.participationRequest.dto.UpdateEventAdminRequest;
import ru.practicum.participationRequest.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByIdAndUserId(Long userId, Long eventId);

    EventFullDto updateEventByIdAndUserId(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    // Admin
    List<EventFullDto> getEventsAdmin(List<Long> users,
                                      List<String> states,
                                      List<Long> categories,
                                      String rangeStart,
                                      String rangeEnd,
                                      int from,
                                      int size);


    EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    // Public
    List<EventShortDto> getEventsPublic(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        String rangeStart,
                                        String rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        int from,
                                        int size,
                                        HttpServletRequest request);

    EventFullDto getEventByIdPublic(Long id, HttpServletRequest request);
}
