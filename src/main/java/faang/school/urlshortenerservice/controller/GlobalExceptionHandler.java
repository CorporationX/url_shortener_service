package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.exception.ValidationErrorResponse;
import faang.school.urlshortenerservice.exception.Violation;
import faang.school.urlshortenerservice.exception.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.error("EntityNotFoundException occurred: {}", e.getMessage(), e);
        return returnedErrorResponse(e, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("IllegalArgumentException occurred: {}", e.getMessage(), e);
        return returnedErrorResponse(e, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handlerConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error("ConstraintViolationException occurred: {}", e.getMessage(), e);
        List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();

        return returnedValidationErrorResponse(e, request, violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException occurred: {}", e.getMessage(), e);
        List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .toList();

        return returnedValidationErrorResponse(e, request, violations);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerIOException(IOException e, HttpServletRequest request) {
        log.error("IOException occurred: {}", e.getMessage(), e);
        return returnedErrorResponse(e, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerException(Exception e, HttpServletRequest request) {
        log.error("Exception occurred: {}", e.getMessage(), e);
        return returnedErrorResponse(e, request);
    }

    private ErrorResponse returnedErrorResponse(Exception exception, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .message(exception.getMessage())
                .build();
    }

    private ValidationErrorResponse returnedValidationErrorResponse(Exception exception,
                                                                    HttpServletRequest request, List<Violation> violations) {
        return ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .message(exception.getMessage())
                .violations(violations)
                .build();
    }
}
