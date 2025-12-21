package ru.practicum.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;

import java.util.List;

// Результат подтверждения/отклонения заявок на участие в событии
@Getter
@Setter
public class EventRequestStatusUpdateResult {

    // Заявка на участие в событии подтвержденная
    private List<ParticipationRequestDto> confirmedRequests;

    // Заявка на участие в событии отклоненная
    private List<ParticipationRequestDto> rejectedRequests;

    public EventRequestStatusUpdateResult(List<ParticipationRequestDto> confirmedRequests,
                                          List<ParticipationRequestDto> rejectedRequests) {
        this.confirmedRequests = confirmedRequests;
        this.rejectedRequests = rejectedRequests;
    }
}
