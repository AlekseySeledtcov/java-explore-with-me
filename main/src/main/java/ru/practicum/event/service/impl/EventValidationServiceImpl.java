package ru.practicum.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.service.EventSearchService;
import ru.practicum.event.service.EventValidationService;
import ru.practicum.exceptions.BadRequestException;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventValidationServiceImpl implements EventValidationService {

    private final EventSearchService eventSearchService;

    @Override
    public void assertAtLeastTwoHoursFromNow(String eventDateTimeStr) {
        LocalDateTime targetDateTime = eventSearchService.parseToLocalDateTime(eventDateTimeStr);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, targetDateTime);

        if (duration.toHours() <= 2) {
            throw new BadRequestException("Событие должно содержать дату, не раньше 2-х часов после добавления события");
        }
    }

    @Override
    public void assertAtLeastOneHoursFromNow(String eventDateTimeStr) {
        LocalDateTime targetDateTime = eventSearchService.parseToLocalDateTime(eventDateTimeStr);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, targetDateTime);

        if (duration.toHours() <= 1) {
            throw new BadRequestException("Событие должно содержать дату, не раньше 1 часа после добавления события");
        }
    }
}
