package faang.school.urlshortenerservice.controller;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
    }

    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        log.error(e.getMessage(), e);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Bad request: " + e.getMessage());
    }

    private ResponseEntity<String> buildResponseEntity(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(message);
    }
}
