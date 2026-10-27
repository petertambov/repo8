package com.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.dto.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class MyExceptionHandler {

    @ExceptionHandler(MyException.class)
    public ResponseEntity<ErrorResponse> handleExceptions(MyException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse(exception.getStatusCode().toString(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Неожиданная ошибка", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Внутренняя ошибка сервера"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message;
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            message = String.format("Некорректное значение '%s' для параметра '%s'. Допустимые значения: %s",
                    ex.getValue(),
                    ex.getName(),
                    Arrays.toString(ex.getRequiredType().getEnumConstants()));
        } else {
            message = String.format("Некорректный тип параметра '%s': ожидался %s, получен %s",
                    ex.getName(),
                    ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "неизвестный",
                    ex.getValue());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("TYPE_MISMATCH", message));
    }
}