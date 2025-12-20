package ru.practicum.event.service;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

public interface EventSearchService {
    /**
     * Преобразует строку с датой в объект LocalDateTime согласно заданному формату.
     *
     * @param date строка с датой в формате, определённом в DATE_TIME_PATTERN
     * @return LocalDateTime, соответствующий входной строке
     * @throws BadRequestException если дата null, пустая или имеет неверный формат
     */
    LocalDateTime parseToLocalDateTime(String date);

    /**
     * Формирует спецификацию для поиска событий в административном интерфейсе.
     *
     * @param users      список ID пользователей-инициаторов
     * @param states     список строковых представлений статусов
     * @param categories список ID категорий
     * @param rangeStart начало диапазона дат
     * @param rangeEnd   конец диапазона дат
     * @return спецификация (Specification<Event>)
     */
    Specification<Event> getEventsAdminSpecification(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd);

    /**
     * Формирует спецификацию для поиска событий в публичном интерфейсе.
     *
     * @param text          поисковый текст
     * @param categories    список ID категорий
     * @param paid          признак платности события
     * @param rangeStart    начало диапазона дат
     * @param rangeEnd      конец диапазона дат
     * @param onlyAvailable флаг «только доступные события»
     * @return спецификация (Specification<Event>)
     * @throws BadRequestException если диапазон дат некорректен (начало после конца)
     */
    Specification<Event> getEventsPublicSpecification(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable);
}
