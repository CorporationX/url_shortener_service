package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.ErrorResponse;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.exception.RedirectException;
import faang.school.urlshortenerservice.exception.ValidationErrorResponse;
import faang.school.urlshortenerservice.exception.Violation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;


import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse onNotFoundException(NotFoundException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RedirectException.class)
    public ErrorResponse onRedirectException(RedirectException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e
    ) {
        List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> Violation.builder()
                        .fieldName(violation.getPropertyPath().toString())
                        .message(violation.getMessage())
                        .build())
                .toList();
        return new ValidationErrorResponse(violations);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        List<Violation> violations = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> Violation.builder()
                        .fieldName(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();
        return new ValidationErrorResponse(violations);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse onRuntimeException(RuntimeException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}