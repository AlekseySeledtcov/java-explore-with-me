package ru.practicum.event.service;

import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.NotFoundException;

import java.util.Optional;

public interface EventCommonService {

    /**
     * Поиск события по ID.
     *
     * @param id идентификатор события
     * @return Optional с событием, если найдено
     */
    Optional<Event> findById(Long id);

    /**
     * Проверяет, существует ли событие с указанным ID, созданное указанным пользователем.
     *
     * @param eventId     ID события
     * @param initiatorId ID инициатора события
     * @return true, если событие существует и принадлежит инициатору, иначе false
     */
    boolean existsByIdAndInitiatorId(long eventId, long initiatorId);

    /**
     * Получает событие по ID и ID инициатора, либо выбрасывает исключение.
     *
     * @param eventId ID события
     * @param userId  ID пользователя‑инициатора
     * @return найденное событие
     * @throws NotFoundException если событие не найдено или пользователь не является инициатором
     */
    Event findByEventIdAndInitiatorIdOrThrow(Long eventId, Long userId);

    /**
     * Проверяет, существуют ли события, привязанные к указанной категории.
     *
     * @param categoryId ID категории
     * @return true, если есть события в указанной категории, иначе false
     */
    boolean existsEventByCategoryId(Long categoryId);

    /**
     * Получает событие по ID или выбрасывает исключение
     *
     * @param eventId ID события
     * @return найденое событие
     * @throws NotFoundException если событие не найдено
     */

    Event getEventByIdAndStateOrThrow(Long eventId, State state);
}
