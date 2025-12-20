package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.annotations.DateTimeFormat;
import ru.practicum.annotations.EnumValue;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.service.EventService;
import ru.practicum.participationRequest.dto.UpdateEventAdminRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/admin/events")
public class EventControllerAdmin {

    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventFullDto> getEventsAdmin(
            @RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<@EnumValue(enumClass = State.class) String> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @DateTimeFormat @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @DateTimeFormat @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {

        log.debug("Поиск событий по по параметрам:\n" +
                "users \t\t{} \n" +
                "states \t\t{} \n" +
                "categories \t{} \n" +
                "rangeStart \t{} \n" +
                "rangeEnd \t{} \n" +
                "from \t\t{} \n" +
                "size \t\t{} \n", users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(
            @Positive @PathVariable(value = "eventId", required = true) Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {

        log.debug("Редактирование данных события с id ={} и его статуса\n" +
                "тело запроса {}", eventId, updateEventAdminRequest);

        return eventService.patchEvent(eventId, updateEventAdminRequest);
    }
}
