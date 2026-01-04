package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.EditCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.PatchCommentDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/comments/")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    /**
     * Добавляет новый комментарий к событию.
     * Если пользователь является организатором события, то комментарий добавляется со статусом PUBLISHED.
     * Если пользователь не является организатором события, то комментарий добавляется со статусом PENDING.
     *
     * @param userId         Идентификатор пользователя, оставляющего комментарий.
     * @param newCommentsDto Данные комментария.
     * @return DTO добавленного комментария с заполненными полями.
     * @throws MethodArgumentNotValidException если DTO невалидно.
     * @throws BadRequestException             если пользователь или событие не найдены
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("user/{userId}")
    public CommentDto addComment(
            @Positive @PathVariable(value = "userId") Long userId,
            @Valid @RequestBody NewCommentDto newCommentsDto) {

        log.debug("add comments id={}, body={}", userId, newCommentsDto);

        return commentService.addComment(userId, newCommentsDto);
    }

    /**
     * Редактирует текст комментария, при условии что комментарий находится в состоянии PENDING.
     * Если комментарий находится в состоянии PUBLISHED, то редактирует его и меняет состояние на PENDING.
     * Если комментарий находится в состоянии CANCELED, то выбрасывается исключение.
     *
     * @param userId         Идентификатор пользователя, оставляющего комментарий.
     * @param editCommentDto DTO с новым текстом комментария и его идентификатором
     * @return DTO измененного комментария
     * @throws MethodArgumentNotValidException если DTO невалидно.
     * @throws NotFoundException               если комментарий не найденю
     */
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("user/{userId}")
    public CommentDto editComment(
            @Positive @PathVariable(value = "userId") Long userId,
            @Valid @RequestBody EditCommentDto editCommentDto) {

        log.debug("edit comment with id={}, body={}", editCommentDto.getCommentId(), editCommentDto);

        return commentService.editComment(userId, editCommentDto);
    }

    /**
     * Меняет статус комментария. Изменить можно только комментарии со статусом PENDING
     *
     * @param userId          идентификатор пользователя.
     * @param patchCommentDto объект с данными для частичного обновления комментария.
     * @return обновлённый объект комментария в формате DTO
     * @throws MethodArgumentNotValidException если данные в patchCommentDto не прошли валидацию.
     * @throws NotFoundException               если комментарий с указанным ID не существует в системе.
     * @throws BadRequestException             если если указан некорректный ID пользователя.
     */
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("admin/{userId}")
    public CommentDto patchComment(
            @Positive @PathVariable(value = "userId") Long userId,
            @Valid @RequestBody PatchCommentDto patchCommentDto) {

        log.debug("patch comment with id={}, body={}", patchCommentDto.getCommentId(), patchCommentDto);

        return commentService.patchComment(userId, patchCommentDto);
    }

    /**
     * Удаляет комменатрий принадлежащий пользователю.
     *
     * @param userId    ID пользователя.
     * @param commentId ID комментария.
     * @throws NotFoundException если комментарий не найден.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("user/{userId}/comment/{commentId}")
    public void deleteComment(
            @Positive @PathVariable(value = "userId") Long userId,
            @Positive @PathVariable(value = "commentId") Long commentId) {

        log.debug("delete comment with id={}", commentId);

        commentService.deleteComment(userId, commentId);
    }

    /**
     * Получает комментарий по его ID.
     *
     * @param commentId ID комментария.
     * @return DTO комментария.
     * @throws NotFoundException если комментарий не найден.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{commentId}")
    public CommentDto getCommentById(
            @Positive @PathVariable(value = "commentId") Long commentId) {

        log.debug("get comment with id={}", commentId);

        return commentService.getCommentById(commentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("event/{eventId}")
    public List<CommentDto> getCommentsByEventId(
            @Positive @PathVariable(value = "eventId") Long eventId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        log.debug("get List comments by eventId");

        return commentService.getCommentsByEventId(eventId, from, size);
    }

}
