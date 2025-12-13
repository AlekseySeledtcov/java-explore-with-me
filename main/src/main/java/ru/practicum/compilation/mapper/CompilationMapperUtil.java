package ru.practicum.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.NotFoundException;

@Named("CompilationMapperUtil")
@Component
@RequiredArgsConstructor
public class CompilationMapperUtil {

    private final EventRepository eventRepository;

    @Named("getEventById")
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }


}
