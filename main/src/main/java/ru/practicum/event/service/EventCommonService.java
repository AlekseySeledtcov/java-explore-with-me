package ru.practicum.event.service;

import ru.practicum.event.model.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface EventCommonService {

    Optional<Event> findById(Long id);

    boolean existsByIdAndInitiatorId(long eventId, long initiatorId);

    Event findByEventIdAndInitiatorIdOrThrow(Long eventId, Long userId);

    boolean existsEventByCategoryId(Long categoryId);

    HashMap<Long, Long> getViews(List<Event> events);
}
