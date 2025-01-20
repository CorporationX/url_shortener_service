package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.ErrorResponse;
import faang.school.urlshortenerservice.exception.RedissonException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DataValidationException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(Exception e) {
        logException(e, LogLevel.WARN);
        return buildResponse(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(errorMessage)
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(EntityNotFoundException e) {
        logException(e, LogLevel.WARN);
        return buildResponse(e);
    }

    @ExceptionHandler({Exception.class, RedissonException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerExceptions(Exception e) {
        logException(e, LogLevel.ERROR);
        return ErrorResponse.builder()
                .message("Internal server error!")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private ErrorResponse buildResponse(Exception e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void logException(Exception e, LogLevel logLevel) {
        String logMessage = "%s: %s".formatted(e.getClass().getSimpleName(), e.getMessage());
        switch (logLevel) {
            case ERROR -> log.error(logMessage, e);
            case WARN -> log.warn(logMessage, e);
        }
    }

    private enum LogLevel {
        ERROR, WARN
    }
}
