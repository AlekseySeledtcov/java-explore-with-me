package ru.practicum.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
DTO ответ
 */

@Getter
@Setter
public class CommentDto {

    private Long id;
    private String text;
    private String state;
    private Long authorId;
    private Long eventId;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
}
