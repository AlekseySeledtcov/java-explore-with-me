package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/*
DTO добаления комментария
 */
@Getter
@Setter
public class NewCommentDto {

    // Текст комментария
    @NotBlank
    @Size(min = 1, max = 2000)
    private String text;

    // ID события, которому добавляется комментарий
    @NotNull
    private Long eventId;
}
