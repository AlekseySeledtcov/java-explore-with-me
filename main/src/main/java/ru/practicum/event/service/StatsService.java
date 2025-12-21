package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.model.Event;

import java.util.HashMap;
import java.util.List;

public interface StatsService {

    /**
     * Регистрирует посещение (хит) на основе HTTP-запроса.
     * Формирует EndpointHitDto с данными:
     * - название приложения (app);
     * - URI запроса;
     * - IP-адрес клиента;
     * - текущее время.
     *
     * @param request HTTP-запрос, на основе которого формируется хит
     */
    void postHit(HttpServletRequest request);

    /**
     * Получает карту соответствия ID событий и количества их просмотров.
     * Для списка событий:
     * - определяет минимальный момент создания (start);
     * - определяет максимальный момент даты события (end);
     * - формирует список URL-адресов событий;
     * - запрашивает статистику у StatsClient;
     * - преобразует результат в карту (eventId → hits).
     *
     * @param events список событий, для которых требуется получить статистику просмотров
     * @return карта, где ключ — ID события, значение — количество просмотров (hits)
     */
    HashMap<Long, Long> getMapCountViewByEvents(List<Event> events);

    /**
     * Получает количество просмотров для конкретного события.
     * Формирует запрос к StatsClient с параметрами:
     * - start: момент создания события;
     * - end: дата события;
     * - url: URL события (/events/{id});
     * - unique: true (уникальные просмотры).
     *
     * @param event событие, для которого требуется получить количество просмотров
     * @return количество просмотров (hits) для указанного события, 0 если данных нет
     */
    Long getCountViewByEvent(Event event);
}
