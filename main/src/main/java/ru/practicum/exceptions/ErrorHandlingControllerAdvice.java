package ru.practicum.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exceptions.model.ApiError;
import ru.practicum.utils.DateTimeConstant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAlreadyExistsException(AlreadyExistsException exception) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.CONFLICT.toString());
        error.setReason("Integrity constraint has been violated.");
        error.setMessage(exception.getMessage());
        error.setTimestamp(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(DateTimeConstant.DATE_TIME_PATTERN)));

        error.setErrors(errors(exception.getStackTrace()));
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException exception) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.NOT_FOUND.toString());
        error.setReason("The required object was not found.");
        error.setMessage(exception.getMessage());
        error.setTimestamp(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(DateTimeConstant.DATE_TIME_PATTERN)));

        error.setErrors(errors(exception.getStackTrace()));
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException exception) {

        ApiError error = new ApiError();

        List<String> errorMessages = exception.getConstraintViolations().stream()
                .map(violations -> {
                    String fieldName = null;
                    for (Path.Node node : violations.getPropertyPath()) {
                        fieldName = node.getName();
                    }
                    return String.format("Field: %s. Error: %s. Value: %s",
                            violations.getPropertyPath(),
                            violations.getMessage(),
                            violations.getInvalidValue());
                })
                .toList();

        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        error.setReason("Incorrectly made request.");
        error.setMessage(errorMessages.toString());
        error.setTimestamp(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(DateTimeConstant.DATE_TIME_PATTERN)));
        error.setErrors(errors(exception.getStackTrace()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException exception) {

        ApiError error = new ApiError();

        List<String> errorMessages = exception.getBindingResult().getAllErrors().stream()
                .map(errorObj -> {
                    if (errorObj instanceof FieldError fieldError) {
                        String fieldName = fieldError.getField();
                        fieldName = fieldName.replaceAll("\\[\\d+\\]", "");
                        if (fieldName.contains(".")) {
                            fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
                        }
                        return String.format("Field: %s. Error: %s. Value: %s",
                                fieldName,
                                fieldError.getDefaultMessage(),
                                fieldError.getRejectedValue());
                    } else {
                        return String.format("Object error: %s", errorObj.getDefaultMessage());
                    }
                })
                .toList();

        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        error.setReason("Incorrectly made request.");
        error.setMessage(String.join(", ", errorMessages));
        error.setTimestamp(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(DateTimeConstant.DATE_TIME_PATTERN)));
        error.setErrors(errors(exception.getStackTrace()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException exception) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        error.setReason("Incorrectly made request.");
        error.setMessage(exception.getMessage());
        error.setTimestamp(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(DateTimeConstant.DATE_TIME_PATTERN)));

        error.setErrors(errors(exception.getStackTrace()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiError> handleRateLimitException(RateLimitException exception) {

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.TOO_MANY_REQUESTS.toString());
        error.setReason("Request limit exceeded");
        error.setMessage(exception.getMessage());
        error.setTimestamp(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(DateTimeConstant.DATE_TIME_PATTERN)));

        error.setErrors(errors(exception.getStackTrace()));
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(error);
    }

    private List<String> errors(StackTraceElement[] stackTrace) {
        if (stackTrace == null || stackTrace.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(stackTrace)
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
}

