package ru.practicum.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventSpecification;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventSearchService;
import ru.practicum.event.service.EventStateService;
import ru.practicum.exceptions.BadRequestException;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.utils.DateTimeConstant.DATE_TIME_PATTERN;

@Service
@RequiredArgsConstructor
public class EventSearchServiceImpl implements EventSearchService {

    private final EventStateService eventStateService;

    @Override
    public LocalDateTime parseToLocalDateTime(String date) {
        if (date == null || date.isBlank()) {
            throw new BadRequestException("Дата не может быть null или пустой");
        }
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        } catch (DateTimeException exception) {
            throw new BadRequestException(
                    String.format("Неверный формат даты '%s'. Ожидаемый формат: %s", date, DATE_TIME_PATTERN)
            );
        }
    }


    @Override
    public Specification<Event> getEventsAdminSpecification(List<Long> users,
                                                            List<String> states,
                                                            List<Long> categories,
                                                            String rangeStart,
                                                            String rangeEnd) {

        List<State> stateEnum = eventStateService.parseStates(states);
        LocalDateTime start = parseToLocalDateTimeWithDefaultValue(rangeStart, null);
        LocalDateTime end = parseToLocalDateTimeWithDefaultValue(rangeEnd, null);

        return Specification.where(EventSpecification.byUsers(users)
                .and(EventSpecification.byState(stateEnum))
                .and(EventSpecification.byCategories(categories))
                .and(EventSpecification.byStartDate(start))
                .and(EventSpecification.byEndDate(end)));
    }


    @Override
    public Specification<Event> getEventsPublicSpecification(String text,
                                                             List<Long> categories,
                                                             Boolean paid,
                                                             String rangeStart,
                                                             String rangeEnd,
                                                             Boolean onlyAvailable) {

        LocalDateTime start = parseToLocalDateTimeWithDefaultValue(rangeStart, LocalDateTime.now());
        LocalDateTime end = parseToLocalDateTimeWithDefaultValue(rangeEnd,
                LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        if (start.isAfter(end)) throw new BadRequestException("The start date cannot be after the end date.");
        if (end.isBefore(start)) throw new BadRequestException("The end date cannot be earlier than the start date.");
        return Specification.where(EventSpecification.isPublished())
                .and(EventSpecification.textContains(text))
                .and(EventSpecification.byCategories(categories))
                .and(EventSpecification.isPaid(paid))
                .and(EventSpecification.byStartDate(start))
                .and(EventSpecification.byEndDate(end))
                .and(EventSpecification.isOnlyAvailable(onlyAvailable));

    }

    private LocalDateTime parseToLocalDateTimeWithDefaultValue(String date, LocalDateTime defaultValue) {
        if (date != null && !date.isBlank()) {
            return parseToLocalDateTime(date);
        } else {
            return defaultValue;
        }
    }
}
