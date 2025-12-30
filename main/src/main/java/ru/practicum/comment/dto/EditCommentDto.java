package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditCommentDto {

    // ID комментария
    @NotNull
    private Long commentId;

    // Текст комментария
    @Size(min = 1, max = 2000)
    @NotBlank
    private String text;
}
