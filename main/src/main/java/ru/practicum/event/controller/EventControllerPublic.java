package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.annotations.DateTimeFormat;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventControllerPublic {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsPublic(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @DateTimeFormat @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @DateTimeFormat @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", required = false) String sort,
            @Min(0) @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            HttpServletRequest request) {

        log.debug("Получение событий с возможностью фильтрации, публичный эндпонинт \n" +
                "text = {}\n" +
                "categories = {}\n" +
                "paid = {}\n" +
                "rangeStart = {}\n" +
                "rangeEnd = {}\n" +
                "onlyAvailable = {}\n" +
                "sort = {}\n" +
                "from = {}\n" +
                "size = {}\n", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        List<EventShortDto> response = eventService.getEventsPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventByIdPublic(
            @Positive @PathVariable(value = "id") Long id,
            HttpServletRequest request) {

        log.debug("Получение информации о событии по id={} , публичный эндпоинт", id);

        EventFullDto response = eventService.getEventByIdPublic(id, request);

        return ResponseEntity.ok().body(response);
    }

}
