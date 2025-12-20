package ru.practicum.event.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.event.enums.State;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.enums.StateActionUser;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventStateService;
import ru.practicum.exceptions.AlreadyExistsException;
import ru.practicum.exceptions.BadRequestException;

import java.util.List;

@Service
public class EventStateServiceImpl implements EventStateService {

    @Override
    public State setStateByStateActionUser(StateActionUser action) {
        return action == StateActionUser.SEND_TO_REVIEW ? State.PENDING : State.CANCELED;
    }

    @Override
    public List<State> parseStates(List<String> states) {
        if (states == null || states.isEmpty()) {
            return null;
        }

        try {
            return states.stream()
                    .map(State::valueOf)
                    .toList();
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Некорректное значение State");
        }
    }

    @Override
    public Event setStatByStateActionAdmin(Event event, StateActionAdmin action) {
        switch (action) {
            case StateActionAdmin.PUBLISH_EVENT -> handlePublishEvent(event);
            case StateActionAdmin.REJECT_EVENT -> handleRejectEvent(event);
        }
        return event;
    }

    private void handlePublishEvent(Event event) {
        State currentState = event.getState();

        if (currentState == State.PUBLISHED || currentState == State.CANCELED) {
            throw new AlreadyExistsException(
                    "Cannot publish the event because it's not in the right state: PUBLISHED");
        }

        if (currentState == State.PENDING) {
            event.setState(State.PUBLISHED);
        }
    }

    private void handleRejectEvent(Event event) {
        State currentState = event.getState();

        if (currentState == State.PUBLISHED || currentState == State.CANCELED) {
            throw new AlreadyExistsException(
                    "Cannot decline event: it has already been published");
        }

        if (currentState == State.PENDING) {
            event.setState(State.CANCELED);
        }
    }
}
