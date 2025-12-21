package ru.practicum.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventCommonService;
import ru.practicum.exceptions.NotFoundException;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapperUtil {

    private final EventCommonService eventCommonService;
    private final CategoryService categoryService;

    public Set<Event> mapEventIdsToEvents(Set<Long> eventIds) {
        System.out.println("List method called with: " + eventIds);
        if (eventIds == null || eventIds.isEmpty()) {
            System.out.println("Returning null for List");
            return Collections.emptySet();
        }

        return eventIds.stream()
                .map(id -> eventCommonService.findById(id)
                        .orElseThrow(() -> new NotFoundException("Event not found: " + eventIds)))
                .collect(Collectors.toSet());
    }
}
