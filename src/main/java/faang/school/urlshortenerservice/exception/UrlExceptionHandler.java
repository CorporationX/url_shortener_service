package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ErrorRepose;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRepose handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error ->
                        String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        log.error("Method argument not valid: {}", errorMessage, e);
        return new ErrorRepose(errorMessage, LocalDateTime.now());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRepose handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Constraint violation: {}", e.getMessage(), e);
        return new ErrorRepose(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRepose handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument: {}", e.getMessage(), e);
        return new ErrorRepose(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(HashRetrievalTimeoutException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorRepose handleHashRetrievalTimeoutException(HashRetrievalTimeoutException e) {
        log.error("HashRetrievalTimeoutException occurred: {}", e.getMessage(), e);
        return new ErrorRepose(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRepose handleUrlNotFoundException(UrlNotFoundException e) {
        log.error("URL not found: {}", e.getMessage(), e);
        return new ErrorRepose(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(InvalidUrlFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRepose handleInvalidUrlFormatException(InvalidUrlFormatException e) {
        log.error("Invalid URL format: {}", e.getMessage(), e);
        return new ErrorRepose(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorRepose handleException(Exception e) {
        log.error("Unexpected exception: {}", e.getMessage(), e);
        return new ErrorRepose(e.getMessage(), LocalDateTime.now());
    }
}
