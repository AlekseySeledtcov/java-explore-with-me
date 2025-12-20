package ru.practicum.event.service;

import ru.practicum.exceptions.BadRequestException;

public interface EventValidationService {
    /**
     * Проверяет, что указанная дата/время события наступает не ранее чем через 2 часа от текущего момента.
     *
     * @param eventDateTimeStr строка с датой и временем события в допустимом формате
     *                         (например, "2025-12-18 18:00")
     * @throws BadRequestException если:
     *                             - строка не может быть преобразована в LocalDateTime;
     *                             - parsed время ≤ (текущее время + 2 часа).
     */
    void assertAtLeastTwoHoursFromNow(String eventDateTimeStr);

    /**
     * Проверяет, что указанная дата/время события наступает не ранее чем через 1 час от текущего момента.
     *
     * @param eventDateTimeStr строка с датой и временем события в допустимом формате
     *                         (например, "2025-12-18 18:00")
     * @throws BadRequestException если:
     *                             - строка не может быть преобразована в LocalDateTime;
     *                             - parsed время ≤ (текущее время + 1 час).
     */
    void assertAtLeastOneHoursFromNow(String eventDateTimeStr);
}
