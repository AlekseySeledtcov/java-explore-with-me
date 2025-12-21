package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.location " +
            "WHERE e.id = :eventId " +
            "AND e.initiator.id = :userId")
    Optional<Event> findByIdAndInitiatorId(
            @Param("eventId") long eventId,
            @Param("userId") long userId);

    @Query("SELECT e FROM Event AS e JOIN FETCH e.location WHERE e.id = :id")
    Optional<Event> findByIdWithLocation(@Param("id") long id);

    @Override
    @EntityGraph(value = "Event.withLocation", type = EntityGraph.EntityGraphType.LOAD)
    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.location " +
            "WHERE e.id = :id " +
            "AND e.state = :state")
    Optional<Event> findByIdAndState(
            @Param("id") Long id,
            @Param("state") State state);

    boolean existsByCategoryId(long categoryId);

    boolean existsByIdAndInitiatorId(long eventId, long initiatorId);
}

