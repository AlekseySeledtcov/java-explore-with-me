package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Данные нового пользователя
@Getter
@Setter
public class NewUserRequest {

    // Почтовый адрес
    @NotNull
    @NotBlank
    @Email(message = "Некорректный email")
    @Size(min = 6, max = 254)
    private String email;

    // Имя
    @NotBlank(message = "Не указано имя")
    @Size(min = 2, max = 250)
    private String name;

}
