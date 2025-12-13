package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

// Подборка событий
@Getter
@Setter
public class NewCompilationDto {

    // Список идентификаторов событий входящих в подборку (уникальный)
    @UniqueElements(message = "Элементы списка событий должны быть уникальными")
    private List<Long> events;

    // Закреплена ли подборка на главной странице сайта
    private Boolean pinned = false;

    // Заголовок подборки
    @NotBlank
    @Size(min = 1, max = 50, message = "Заголовок должен быть от 1 до 50 символов")
    private String title;

    @Override
    public String toString() {
        return "NewCompilationDto{" +
                "events=" + events +
                ", pinned=" + pinned +
                ", title='" + title + '\'' +
                '}';
    }
}
