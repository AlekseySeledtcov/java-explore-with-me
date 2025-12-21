package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.participationRequest.dto.UpdateEventUserRequest;
import ru.practicum.participationRequest.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerUser {

    private final EventService eventService;
    private final RequestService requestService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventShortDto> getEventsByUserId(
            @Positive @PathVariable(value = "userId") Long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        log.debug("Запрос на получение списка событий добавленных пользователем с id={}", userId);

        return eventService.getEventsByUserId(userId, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto createEvent(
            @Positive @PathVariable(value = "userId") Long userId,
            @Valid @RequestBody NewEventDto newEventDto) {

        log.debug("Запрос на добавление нового события\n" +
                "Тело запроса {}\n" +
                "Идентификатор id={}", newEventDto, userId);

        return eventService.createEvent(userId, newEventDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdAndUserId(
            @Positive @PathVariable(value = "userId") Long userId,
            @Positive @PathVariable(value = "eventId") Long eventId) {

        log.debug("Запрос события с id={} от пользователя с id={}", eventId, userId);

        return eventService.getEventByIdAndUserId(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByIdAndUserId(
            @Positive @PathVariable(value = "userId") Long userId,
            @Positive @PathVariable(value = "eventId") Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest) {

        log.debug("Запрос обновления событи с id={} от пользователя с id={}, тело запроса: {}", userId, eventId, updateRequest);

        return eventService.updateEventByIdAndUserId(userId, eventId, updateRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(
            @Positive @PathVariable(value = "userId") Long userId,
            @Positive @PathVariable(value = "eventId") Long eventId) {

        log.debug("Запрос списка запросов на участие: userId={}, eventId={}", userId, eventId);

        return requestService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchStatusRequestsByUserIdAndEventId(
            @Positive @PathVariable(value = "userId") Long userId,
            @Positive @PathVariable(value = "eventId") Long eventId,
            @NotNull @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {

        log.debug("Запрос изменения статуса заявок на участие в событии eventId={}, пользователя userid={}", eventId, userId);

        return requestService.patchStatusRequestsByUserIdAndEventId(userId, eventId, updateRequest);
    }
}