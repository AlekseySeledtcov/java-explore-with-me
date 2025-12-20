package ru.practicum.participationRequest.service;

import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;

import java.util.HashMap;
import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUserIdAndEventId(long userId, long eventId);

    EventRequestStatusUpdateResult patchStatusRequestsByUserIdAndEventId(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest updateRequest);

    long getCountConfirmedRequestsByEventId(long eventId);

    HashMap<Long, Long> getConfirmedRequestsCount(List<Long> eventIds);
}

