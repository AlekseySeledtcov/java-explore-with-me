package ru.practicum.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.practicum.validators.EnumValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface EnumValue {
    Class<? extends Enum> enumClass();

    String message() default "Указан не корректный статус события";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
