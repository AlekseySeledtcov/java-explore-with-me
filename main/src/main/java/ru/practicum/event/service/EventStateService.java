package ru.practicum.event.service;

import ru.practicum.event.enums.State;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.enums.StateActionUser;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.AlreadyExistsException;
import ru.practicum.exceptions.BadRequestException;

import java.util.List;

public interface EventStateService {
    /**
     * Определяет новый статус события на основе пользовательского действия.
     *
     * @param action действие пользователя (SEND_TO_REVIEW или CANCEL_REQUEST)
     * @return новый статус события:
     *         - State.PENDING при действии SEND_TO_REVIEW;
     *         - State.CANCELED при других действиях
     */
    State setStateByStateActionUser(StateActionUser action);

    /**
     * Преобразует список строковых представлений статусов в перечисление State.
     *
     * @param states список строковых значений статусов
     * @return список объектов State или null, если входной список null/пустой
     * @throws BadRequestException если какое‑либо значение не соответствует допустимым статусам
     */
    List<State> parseStates(List<String> states);

    /**
     * Обновляет статус события на основе административного действия.
     * - PUBLISH_EVENT: переводит событие в статус PUBLISHED (если было PENDING);
     * - REJECT_EVENT: переводит событие в статус CANCELED (если было PENDING).
     *
     * @param event событие, статус которого нужно обновить
     * @param action административное действие
     * @return обновлённое событие с новым статусом
     * @throws AlreadyExistsException если действие невозможно выполнить из‑за текущего статуса события
     */
    Event setStatByStateActionAdmin(Event event, StateActionAdmin action);
}
