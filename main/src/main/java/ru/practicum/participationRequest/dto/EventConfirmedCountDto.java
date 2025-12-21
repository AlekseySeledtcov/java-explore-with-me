package ru.practicum.participationRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EventConfirmedCountDto {
    private Long eventId;
    private Long confirmedCount;
}
