package ru.practicum.exceptions.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Сведения об ошибке
@Getter
@Setter
public class ApiError {

    // Код статуса HTTP-ответа
    private String status;

    // Общее описание причины ошибки
    private String reason;

    // Сообщение об ошибке
    private String message;

    // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
    private String timestamp;

    // Список стектрейсов или описания ошибок
    private List<String> errors;
}
