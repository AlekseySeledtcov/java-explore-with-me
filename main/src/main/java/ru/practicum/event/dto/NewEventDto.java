package ru.practicum.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.annotations.DateTimeFormat;
import ru.practicum.location.model.Location;

// Новое событие
@Getter
@Setter
public class NewEventDto {

    // Краткое описание события
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    // id категории к которой относится событие
    @NotNull
    private Long category;

    // Полное описание события
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    // Дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    @NotBlank
    @DateTimeFormat
    private String eventDate;

    // Широта и долгота места проведения события
    @NotNull
    private Location location;

    // Нужно ли оплачивать участие в событии
    private Boolean paid;

    // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    @Min(0)
    private Integer participantLimit;

    // Нужна ли пре-модерация заявок на участие. Если true, то все заявки будут ожидать подтверждения инициатором события. Если false - то будут подтверждаться автоматически.
    private Boolean requestModeration;

    // Заголовок события
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @Override
    public String toString() {
        return "NewEventDto{" +
                "category=" + category +
                ", location=" + location +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", requestModeration=" + requestModeration +
                '}';
    }
}
