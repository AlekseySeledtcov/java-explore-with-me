package ru.practicum.user.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.AlreadyExistsException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserRepository;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.utils.PaginationUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        if (ids == null || ids.isEmpty()) {

            Pageable pageable = PaginationUtils.createPageable(from, size, null);

            Page<User> userPage = userRepository.findAll(pageable);
            return userPage.getContent().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids).stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
    }


    @Transactional
    @Override
    public UserDto postUser(NewUserRequest newUserRequest) {

        if (existsByEmail(newUserRequest.getEmail())) {
            throw new AlreadyExistsException("Пользователь с email " + newUserRequest.getEmail() + " уже существует");
        }

        User user = userRepository.save(userMapper.toEntity(newUserRequest));
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        getUserOrThrow(id);
        userRepository.deleteById(id);
    }

    // Получение пользователя по Id
    @Override
    public User getUserOrThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " was not found"));
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
