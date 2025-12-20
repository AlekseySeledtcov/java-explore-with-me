package ru.practicum.participationRequest.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.annotations.DateTimeFormat;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.location.model.Location;

// Данные для изменения информации о событии. Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.
@Getter
@Setter
public class UpdateEventAdminRequest {

    // Новая аннотация
    @Size(min = 20, max = 2000)
    private String annotation;

    // Новая категория
    private Long category;

    // Новое описание
    @Size(min = 20, max = 7000)
    private String description;

    // Новые дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    @DateTimeFormat
    private String eventDate;

    // Широта и долгота места проведения события
    private Location location;

    // Новое значение флага о платности мероприятия
    private Boolean paid;

    // Новый лимит пользователей
    private Integer participantLimit;

    // Нужна ли пре-модерация заявок на участие
    private Boolean requestModeration;

    // Новое состояние события
    private StateActionAdmin stateAction;

    // Новый заголовок
    @Size(min = 3, max = 120)
    private String title;
}
