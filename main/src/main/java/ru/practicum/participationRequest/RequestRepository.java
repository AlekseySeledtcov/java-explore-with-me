package ru.practicum.participationRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.participationRequest.dto.EventConfirmedCountDto;
import ru.practicum.participationRequest.model.ParticipationRequest;
import ru.practicum.participationRequest.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    @Query("SELECT COUNT(pr) FROM ParticipationRequest AS pr WHERE pr.event.id = :eventId")
    int countByEventId(@Param("eventId") long eventId);

    List<ParticipationRequest> findAllByEventId(long eventId);

    long countByEventIdAndStatus(long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventIdAndIdInAndStatus(long eventId, List<Long> ids, RequestStatus status);

    @Query("SELECT new ru.practicum.participationRequest.dto.EventConfirmedCountDto(r.event.id, COUNT(r)) " +
            "FROM ParticipationRequest r " +
            "WHERE r.event.id IN :eventIds " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id")
    List<EventConfirmedCountDto> findConfirmedCountsByEventIds(@Param("eventIds") List<Long> eventIds);

}

