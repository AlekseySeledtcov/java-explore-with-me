package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventCommonServiceImpl implements EventCommonService {

    private final EventRepository eventRepository;
    @Autowired
    private final StatsClient statsClient;

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByIdAndInitiatorId(long eventId, long initiatorId) {
        return eventRepository.existsByIdAndInitiatorId(eventId, initiatorId);
    }

    @Transactional(readOnly = true)
    @Override
    public Event findByEventIdAndInitiatorIdOrThrow(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found or you are not the initiator"));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsEventByCategoryId(Long categoryId) {
        return eventRepository.existsByCategoryId(categoryId);
    }

    @Override
    public HashMap<Long, Long> getViews(List<Event> events) {
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .get();
        LocalDateTime end = events.stream()
                .map(Event::getEventDate)
                .max(LocalDateTime::compareTo)
                .get();
        List<String> urls = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        List<ViewStatsDto> result = statsClient.getStats(start, end, urls, true);

        return (HashMap<Long, Long>) result.stream()
                .collect(Collectors.toMap(
                        this::getEventIdByUrl,
                        ViewStatsDto::getHits
                ));
    }

    private Long getEventIdByUrl(ViewStatsDto viewStatsDto) {
        String eventId = viewStatsDto.getUri().split("/")[2];
        return Long.parseLong(eventId);
    }
}
