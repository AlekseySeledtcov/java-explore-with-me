package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Категория
@Getter
@Setter
public class CategoryDto {

    // Идентификатор категории
    private Long id;

    // Название категории
    @NotBlank(message = "Имя категории не может быть пустым")
    @Size(min = 1, max = 50)
    private String name;
}
