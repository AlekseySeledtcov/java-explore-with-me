package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Пользователь
@Getter
@Setter
public class UserDto {

    //Почтовый адрес
    @Email(message = "Некорректный email")
    private String email;

    // Идентификатор
    private Long id;

    // Имя
    @NotBlank(message = "Не указано имя")
    private String name;
}
