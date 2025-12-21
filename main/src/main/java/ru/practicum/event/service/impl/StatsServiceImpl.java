package ru.practicum.event.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.StatsService;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsClient statsClient;
    @Value("${app}")
    private String app;

    @Override
    public void postHit(HttpServletRequest request) {
        EndpointHitDto hitDto = new EndpointHitDto(
                app,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());

        statsClient.postHit(hitDto);
    }

    @Override
    public HashMap<Long, Long> getMapCountViewByEvents(List<Event> events) {
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

    @Override
    public Long getCountViewByEvent(Event event) {
        LocalDateTime start = event.getCreatedOn();
        LocalDateTime end = event.getEventDate();
        String url = "/events/" + event.getId();
        List<ViewStatsDto> result = statsClient.getStats(start, end, List.of(url), true);
        return result.stream()
                .findFirst()
                .map(ViewStatsDto::getHits)
                .orElse(0L);
    }

    private Long getEventIdByUrl(ViewStatsDto viewStatsDto) {
        String eventId = viewStatsDto.getUri().split("/")[2];
        return Long.parseLong(eventId);
    }

}
