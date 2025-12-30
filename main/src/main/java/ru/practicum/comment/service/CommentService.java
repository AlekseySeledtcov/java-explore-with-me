package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.EditCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.PatchCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, NewCommentDto newCommentsDto);

    CommentDto editComment(Long userId, EditCommentDto editCommentDto);

    CommentDto patchComment(Long userId, PatchCommentDto patchCommentDto);

    void deleteComment(Long userId, Long commentId);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);
}
