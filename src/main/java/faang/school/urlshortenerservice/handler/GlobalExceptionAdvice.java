package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.UrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        log.error("controller method argument validation error {}", e.getMessage(), e);
        e.getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return errors;
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(DataValidationException e) {
        log.error("Data validation exception, cause: {}, stack trace: {}", e.getCause(), e.getStackTrace(), e);
        return Map.of("error", "validation exception", "message", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> serverFailureExceptionHandler(Exception e) {
        log.error("{}, cause: {}, \n stack trace: {}",
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getCause(), e.getStackTrace(), e);
        return Map.of("error","RESPONSE CODE: 500, INTERNAL SERVER ERROR",
                "message", e.getMessage());
    }

    @ExceptionHandler(UrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> urlExceptionHandler(UrlException e) {
        log.error("Url exception, cause: {}, \n stack trace: {}", e.getCause(), e.getStackTrace(), e);
        return Map.of("error", "Url exception", "message", e.getMessage());
    }
}
