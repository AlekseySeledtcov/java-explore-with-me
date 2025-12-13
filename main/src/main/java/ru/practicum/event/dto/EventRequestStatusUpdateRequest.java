package ru.practicum.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.annotations.EnumValue;
import ru.practicum.participationRequest.model.RequestStatus;

import java.util.List;

// Изменение статуса запроса на участие в событии текущего пользователя
@Getter
@Setter
public class EventRequestStatusUpdateRequest {

    // Идентификаторы запросов на участие в событии текущего пользователя
    @NotNull
    @NotEmpty
    private List<Long> requestIds;

    // Новый статус запроса на участие в событии текущего пользователя
    @EnumValue(enumClass = RequestStatus.class)
    private String status;
}
