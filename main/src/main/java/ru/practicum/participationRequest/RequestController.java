package ru.practicum.participationRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.participationRequest.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(
            @Positive @PathVariable(value = "userId") Long userId) {

        log.debug("Запрос списка заявок текущего пользователя по id {}", userId);
        List<ParticipationRequestDto> result = requestService.getRequests(userId);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(
            @Positive @PathVariable(value = "userId", required = true) Long userId,
            @Min(1) @RequestParam(value = "eventId", required = true) Long eventId) {

        log.debug("Добавление запроса от текущего пользователя с id {}", userId);
        ParticipationRequestDto result = requestService.createRequest(userId, eventId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @Positive @PathVariable(value = "userId", required = true) Long userId,
            @Positive @PathVariable(value = "requestId", required = true) Long requestId) {

        log.debug("Отмена пользаветелем с id {} запроса на участие с id {}", userId, requestId);
        ParticipationRequestDto result = requestService.cancelRequest(userId, requestId);
        return ResponseEntity.ok().body(result);
    }
}
