package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

// Изменение информации о подборке событий. Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.
@Getter
@Setter
public class UpdateCompilationRequest {

    // Список id событий подборки для полной замены текущего списка, элементы списка уникальные
    @UniqueElements(message = "Элементы списка событий должны быть уникальными")
    private List<Long> events;

    // Закреплена ли подборка на главной странице сайта
    private Boolean pinned;

    // Заголовок подборки
    @Size(max = 50)
    private String title;
}
