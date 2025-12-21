package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.participationRequest.dto.UpdateEventAdminRequest;
import ru.practicum.participationRequest.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventEnrichmentService {


    /**
     * Обогащает список событий краткой информацией (EventShortDto), включая:
     * - количество подтверждённых запросов на участие;
     * - количество просмотров.
     *
     * @param events список событий для обогащения
     * @return список DTO событий с дополненной информацией
     */
    List<EventShortDto> enrichmentEventShortDto(List<Event> events);

    /**
     * Создаёт сущность события на основе DTO нового события, дополняя её:
     * - инициатором (пользователем);
     * - категорией;
     * - локацией.
     *
     * @param userId идентификатор пользователя-инициатора
     * @param newEventDto DTO с данными нового события
     * @return заполненная сущность события
     */
    Event enrichmentEventFromNewEventDto(Long userId, NewEventDto newEventDto);

    /**
     * Обогащает событие полной информацией (EventFullDto), включая:
     * - количество подтверждённых запросов на участие;
     * - количество просмотров.
     *
     * @param event событие для обогащения
     * @return DTO события с дополненной информацией
     */
    EventFullDto enrichmentEventFullDto(Event event);

    /**
     * Обновляет событие на основе пользовательского запроса на обновление,
     * дополняя его новой категорией и/или локацией при необходимости.
     *
     * @param event существующее событие
     * @param updateRequest DTO с данными для обновления
     * @return обновлённое событие
     */
    Event enrichmentEventFromUpdateEventUserRequest(Event event, UpdateEventUserRequest updateRequest);

    /**
     * Обогащает список событий полной информацией (EventFullDto), включая:
     * - количество подтверждённых запросов на участие;
     * - количество просмотров.
     *
     * @param events список событий для обогащения
     * @return список DTO событий с дополненной информацией
     */
    List<EventFullDto> enrichmentEventFullDtos(List<Event> events);

    /**
     * Обновляет событие на основе запроса администратора на обновление,
     * дополняя его:
     * - новой категорией (если указана);
     * - новой локацией (если указана);
     * - новым статусом (если указано действие по статусу).
     *
     * @param event существующее событие
     * @param updateRequest DTO с данными для административного обновления
     * @return обновлённое событие
     */
    Event enrichmentEventFromUpdateEventAdminRequest(Event event, UpdateEventAdminRequest updateRequest);
}
