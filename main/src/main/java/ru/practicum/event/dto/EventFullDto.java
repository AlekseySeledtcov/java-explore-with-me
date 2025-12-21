package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.enums.State;
import ru.practicum.location.model.Location;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.utils.DateTimeConstant;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventFullDto {

    // Краткое описание
    @NotBlank
    private String annotation;

    // Категория
    @NotNull
    private CategoryDto category;

    // Количество одобренных заявок на участие в данном событии
    private Long confirmedRequests;

    // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = DateTimeConstant.DATE_TIME_PATTERN)
    private LocalDateTime createdOn;

    // Полное описание события
    private String description;

    // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @NotBlank
    @JsonFormat(pattern = DateTimeConstant.DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    // Идентификатор
    private Long id;

    // Пользователь (краткая информация)
    @NotBlank
    private UserShortDto initiator;

    // Широта и долгота места проведения события
    @NotBlank
    private Location location;

    // Нужно ли оплачивать участие
    @NotBlank
    private Boolean paid;

    // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Integer participantLimit;

    // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = DateTimeConstant.DATE_TIME_PATTERN)
    private LocalDateTime publishedOn;

    // Нужна ли пре-модерация заявок на участие
    private Boolean requestModeration;

    // Список состояний жизненного цикла события
    private State state;

    // Заголовок
    @NotNull
    private String title;

    // Количество просмотров события
    private Long views;

    @Override
    public String toString() {
        return "EventFullDto{" +
                "category=" + category +
                ", confirmedRequests=" + confirmedRequests +
                ", createdOn=" + createdOn +
                ", eventDate=" + eventDate +
                ", id=" + id +
                ", initiator=" + initiator +
                ", location=" + location +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", publishedOn=" + publishedOn +
                ", requestModeration=" + requestModeration +
                ", state=" + state +
                ", views=" + views +
                '}';
    }
}
