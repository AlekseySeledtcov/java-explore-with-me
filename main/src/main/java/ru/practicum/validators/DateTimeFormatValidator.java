package ru.practicum.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.annotations.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeFormatValidator implements ConstraintValidator<DateTimeFormat, String> {
    private DateTimeFormatter formatter;

    @Override
    public void initialize(DateTimeFormat annotation) {
        this.formatter = DateTimeFormatter.ofPattern(annotation.pattern());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        try {
            LocalDateTime.parse(value, formatter);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }
}