package ru.practicum.event.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventCommonService;
import ru.practicum.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapperUtil {

    private final EventCommonService eventCommonService;
    private final CategoryService categoryService;

    @Named("getEventById")
    public List<Event> mapEventIdsToEvents(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) return null;

        return eventIds.stream()
                .map(id -> eventCommonService.findById(id)
                        .orElseThrow(() -> new NotFoundException("Event not found: " + eventIds)))
                .collect(Collectors.toList());
    }
}
