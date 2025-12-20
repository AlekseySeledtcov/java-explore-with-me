package ru.practicum.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventCommonService;
import ru.practicum.exceptions.NotFoundException;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class EventCommonServiceImpl implements EventCommonService {

    private final EventRepository eventRepository;


    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }


    @Override
    public boolean existsByIdAndInitiatorId(long eventId, long initiatorId) {
        return eventRepository.existsByIdAndInitiatorId(eventId, initiatorId);
    }


    @Override
    public Event findByEventIdAndInitiatorIdOrThrow(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found or you are not the initiator"));
    }


    @Override
    public boolean existsEventByCategoryId(Long categoryId) {
        return eventRepository.existsByCategoryId(categoryId);
    }

}
