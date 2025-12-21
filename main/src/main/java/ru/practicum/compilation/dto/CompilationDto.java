package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

// Подборка событий
@Getter
@Setter
public class CompilationDto {

    // Список событий входящих в подборку, значения должны быть уникальными
    private List<EventShortDto> events;

    // Идентификатор
    @NotNull
    private Long id;

    // Закреплена ли подборка на главной странице сайта
    @NotNull
    private Boolean pinned;

    // Заголовок подборки
    @NotBlank
    private String title;
}
