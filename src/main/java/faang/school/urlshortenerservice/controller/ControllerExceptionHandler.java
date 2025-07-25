package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolation(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                errorMessage,
                LocalDateTime.now().format(formatter)
        );
        log.error("Constraint violation. Response [{}]", error, e);
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidData(MethodArgumentNotValidException e) {
        String errorMessage = Arrays.stream(e.getDetailMessageArguments())
                .map(Object::toString)
                .collect(Collectors.joining("; "));
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                errorMessage,
                LocalDateTime.now().format(formatter)
        );
        log.error("Invalid data from request given. Response [{}]", error, e);
        return error;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException e) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                String.format("Resource not found: %s", e.getMessage()),
                LocalDateTime.now().format(formatter)
        );
        log.error("Resource not found. Response: [{}]", error, e);
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                String.format("Unexpected error occurred [%s]", e.getMessage()),
                LocalDateTime.now().format(formatter)
        );
        log.error("Internal error. Response: [{}]", error, e);
        return error;
    }
}
