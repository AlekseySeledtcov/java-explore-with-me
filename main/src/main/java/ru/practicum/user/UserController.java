package ru.practicum.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.Service.UserService;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUser(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        log.debug("Получение информации о пользователях \n" +
                "Список ids={}", ids);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUsers(ids, from, size));
    }


    @PostMapping
    public ResponseEntity<UserDto> postUser(
            @Valid @RequestBody NewUserRequest newUserRequest) {

        log.debug("Добавление нового пользователя \n" +
                "Тело запроса {}", newUserRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.postUser(newUserRequest));
    }


    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUser(
            @Positive @Min(1) @PathVariable(name = "userId") Long id) {

        log.debug("Удаление пользователя с id={}", id);

        userService.deleteUser(id);
        return ResponseEntity
                .noContent()
                .build();
    }

}
