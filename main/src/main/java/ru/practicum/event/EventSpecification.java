package ru.practicum.event;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.participationRequest.model.ParticipationRequest;
import ru.practicum.participationRequest.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecification {

    // Фильтр по создателю события
    public static Specification<Event> byUsers(List<Long> users) {
        return (root, query, criteriaBuilder) -> {
            if (users == null || users.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.in(root.get("initiator").get("id")).value(users);
        };
    }

    // Фильтрация по полю State
    public static Specification<Event> byState(List<State> states) {
        return (root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.in(root.get("state")).value(states);
        };
    }

    // state = PUBLISHED
    public static Specification<Event> isPublished() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), State.PUBLISHED);
    }

    // Текстовый фильтр по полям annotation и description (без учета регистра)
    public static Specification<Event> textContains(String text) {
        return (root, query, criteriaBuilder) -> {
            if (text == null || text.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + text.toLowerCase() + "%";

            return criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern));
        };
    }

    // Фильтр по полю Categories
    public static Specification<Event> byCategories(List<Long> categories) {
        return (root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.in(root.get("category").get("id")).value(categories);
        };
    }

    // фильтр по полю Paid
    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, criteriaBuilder) -> {
            if (paid == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("paid"), paid);
        };
    }

    // Фильтр по дате начала
    public static Specification<Event> byStartDate(LocalDateTime start) {
        return (root, query, criteriaBuilder) -> {
            if (start == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start);
        };
    }

    // Фильтр по дате окончания
    public static Specification<Event> byEndDate(LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (end == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end);
        };
    }

    //Фильтрация по полю onlyAvailable
    public static Specification<Event> isOnlyAvailable(Boolean onlyAvailable) {
        return (root, query, criteriaBuilder) -> {
            if (onlyAvailable == null || !onlyAvailable) {
                return criteriaBuilder.conjunction();
            }
            // Подзапрос для подсчёта подтверждённых заявок
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ParticipationRequest> prRoot = subquery.from(ParticipationRequest.class);

            subquery.select(criteriaBuilder.count(prRoot))
                    .where(
                            criteriaBuilder.equal(prRoot.get("event"), root),
                            criteriaBuilder.equal(prRoot.get("status"), RequestStatus.CONFIRMED)
                    );

            return criteriaBuilder.greaterThan(root.get("participantLimit"), subquery);
        };
    }
}
