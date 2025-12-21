package ru.practicum.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// Пользователь (краткая информация)
@Getter
@Setter
public class UserShortDto {

    // Идентификатор
    @NotNull(message = "Не указан id")
    private Long id;

    // Имя
    @NotBlank(message = "Не указано имя")
    private String name;
}
