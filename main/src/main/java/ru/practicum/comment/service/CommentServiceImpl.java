package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.AntiFloodService;
import ru.practicum.comment.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.EditCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.PatchCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.enums.State;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventCommonService;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.Service.UserService;
import ru.practicum.user.model.User;
import ru.practicum.utils.PaginationUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserService userService;
    private final EventCommonService eventCommonService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AntiFloodService antiFloodService;


    @Transactional
    @Override
    public CommentDto addComment(Long userId, NewCommentDto newCommentsDto) {
        User user = getValidatedUserOrThrow(userId);
        Event event = getValidatedEventOrThrow(newCommentsDto.getEventId());
        State currentState = calculateCommentState(userId, event);

        antiFloodService.checkCommentAllowed(userId);

        Comment comment = commentMapper.toEntity(newCommentsDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setState(currentState);

        if (currentState == State.PUBLISHED) {
            comment.setPublishedOn(LocalDateTime.now());
        }

        comment = commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public CommentDto editComment(Long userId, EditCommentDto editCommentDto) {
        Comment comment = ensureCommentBelongsToUser(editCommentDto.getCommentId(), userId);

        if (comment.getState() == State.PUBLISHED) {
            antiFloodService.checkCommentAllowed(userId);
            commentMapper.editComment(comment, editCommentDto);
            comment.setState(State.PENDING);
        } else if (comment.getState() == State.PENDING) {
            antiFloodService.checkCommentAllowed(userId);
            commentMapper.editComment(comment, editCommentDto);
        } else {
            throw new BadRequestException("Too many requests");
        }

        comment = commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public CommentDto patchComment(Long userId, PatchCommentDto patchCommentDto) {
        getValidatedUserOrThrow(userId);
        Comment comment = getValidatedCommentOrThrow(patchCommentDto.getCommentId());

        StateActionAdmin action = patchCommentDto.getStateActionAdmin();

        applyStateTransition(comment, action);

        return commentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = ensureCommentBelongsToUser(commentId, userId);
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = getValidatedCommentOrThrow(commentId);

        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        Pageable pageable = PaginationUtils.createPageable(from, size, null);

        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    // Проверяем что пользователь есть в базе если нет, то перехватываем исключение и выбрасываем BadRequestException
    private User getValidatedUserOrThrow(Long userid) {
        try {
            return userService.getUserOrThrow(userid);
        } catch (NotFoundException exception) {
            throw new BadRequestException("Invalid request parameter: " + exception.getMessage());
        }
    }

    // Проверяем что событие есть в базе, если нет то перехватываем исключение и выбрасваем BadRequestException
    private Event getValidatedEventOrThrow(Long eventId) {
        try {
            return eventCommonService.getEventByIdAndStateOrThrow(eventId, State.PUBLISHED);
        } catch (NotFoundException exception) {
            throw new BadRequestException("Invalid request parameter: " + exception.getMessage());
        }
    }

    /**
     * Возвращает комментарий по ID после проверки его существования
     *
     * @param commentId идентификатор комментария
     * @return объект комментрий
     * @throws NotFoundException если комментарий с указанным ID не существует в системе
     */
    private Comment getValidatedCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with ID=%d not found", commentId)));
    }

    // Вычесляем статус комментария, и возвращаем его
    private State calculateCommentState(Long userId, Event event) {
        return event.getInitiator().getId().equals(userId)
                ? State.PUBLISHED
                : State.PENDING;
    }

    // Проверяем что комментарий принадлежит данному пользователю, и возвращаем его

    /**
     * Проверяем что комментарий принадлежит данному пользователю, и возвращаем его
     *
     * @param commentId ID комментария
     * @param userId    ID пользователя
     * @return entity Comment
     * @throws NotFoundException если комментарий не найден
     */
    private Comment ensureCommentBelongsToUser(Long commentId, Long userId) {
        return commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with ID %d not found", commentId)
                ));
    }

    /**
     * Применяет переход состояния комментария в соответствии с указанным действием администратора.
     *
     * @param comment изменяемый объект комментария.
     * @param action  действие администратора.
     * @throws BadRequestException если:
     *                             - комментарий уже опубликован (при попытке повторной публикации);
     *                             - комментарий отменён (при попытке публикации отмененного комментария);
     *                             - передано неподдерживаемое действие (в случае расширения enum без обновления switch).
     */

    private void applyStateTransition(Comment comment, StateActionAdmin action) {
        switch (action) {
            case REJECT_EVENT -> comment.setState(State.CANCELED);

            case PUBLISH_EVENT -> {
                if (comment.getState() == State.PUBLISHED) {
                    throw new BadRequestException("The comment is already in the publishing stage. Editing is not possible.");
                }
                if (comment.getState() == State.CANCELED) {
                    throw new BadRequestException("Unable to publish a cancelled comment");
                }
                if (comment.getState() == State.PENDING) {
                    comment.setState(State.PUBLISHED);
                    comment.setPublishedOn(LocalDateTime.now());
                }
            }
            default -> throw new BadRequestException(
                    String.format("Unsupported action: %s", action));
        }
    }
}
