package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Данные для добавления новой категории
@Getter
@Setter
public class NewCategoryDto {

    // Название категории
    @NotNull(message = "Название категории не может быть null")
    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 1, max = 50, message = "Название должно быть от 1 до 50 символов")
    private String name;
}
