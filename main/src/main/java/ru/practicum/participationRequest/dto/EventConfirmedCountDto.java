package ru.practicum.participationRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EventConfirmedCountDto {
    Long eventId;
    Long confirmedCount;
}
