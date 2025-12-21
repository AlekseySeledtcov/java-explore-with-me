package ru.practicum.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.practicum.validators.DateTimeFormatValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeFormatValidator.class)
public @interface DateTimeFormat {
    String pattern() default "yyyy-MM-dd HH:mm:ss";

    String message() default "Некорректный формат даты и времени";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}