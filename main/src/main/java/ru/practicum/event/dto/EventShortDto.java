package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.user.dto.UserShortDto;

import java.util.List;

// Краткая информация о событии
@Getter
@Setter
public class EventShortDto {

    //Краткое описание
    @NotBlank
    private String annotation;

    // Категория
    @NotNull
    private CategoryDto category;

    // Количество одобренных заявок на участие в данном событии
    private Long confirmedRequests;

    // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private String eventDate;

    // Идентификатор
    private Long id;

    // Пользователь (краткая информация)
    @NotNull
    private UserShortDto initiator;

    // Нужно ли оплачивать участие
    @NotNull
    private Boolean paid;

    // Заголовок
    @NotNull
    private String title;

    // Количество просмотрев события
    private Long views;

    // Список комментариев
    private List<CommentDto> comments;
}
