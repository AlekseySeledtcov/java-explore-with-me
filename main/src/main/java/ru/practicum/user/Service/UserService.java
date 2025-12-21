package ru.practicum.user.Service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto postUser(NewUserRequest newUserRequest);

    void deleteUser(Long id);

    User getUserOrThrow(long id);

}
