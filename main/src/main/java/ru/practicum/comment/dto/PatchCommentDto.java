package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.enums.StateActionAdmin;

@Getter
@Setter
@NotNull
public class PatchCommentDto {

    // Идентификаторы комментария
    private Long commentId;

    // Новый статус комментария
    private StateActionAdmin stateActionAdmin;
}
