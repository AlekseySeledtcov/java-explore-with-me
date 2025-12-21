package ru.practicum.participationRequest.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.participationRequest.model.RequestStatus;

// Заявка на участие в событии
@Getter
@Setter
public class ParticipationRequestDto {

    // Дата и время создания заявки
    private String created;

    // Идентификатор события
    private Long event;

    // Идентификатор заявки
    private Long id;

    // Идентификатор пользователя, отправившего заявку
    private Long requester;

    // Статус заявки
    private RequestStatus status;
}
