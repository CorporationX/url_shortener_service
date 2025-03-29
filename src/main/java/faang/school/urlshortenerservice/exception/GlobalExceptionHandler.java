package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.error("Validation failed: {}", errors);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Object> handleUrlNotFoundException(UrlNotFoundException ex) {
        String message = ex.getMessage();
        log.error("Url not found: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, message, null);
    }

    @ExceptionHandler(UniqueNumberOutOfBoundsException.class)
    public ResponseEntity<Object> handleUniqueNumberOutOfBoundsException(UniqueNumberOutOfBoundsException ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    private ResponseEntity<Object> buildErrorResponseEntity(
            HttpStatus status, String message, Map<String, String> errors) {
        ErrorResponse apiError = new ErrorResponse(status.value(), message, errors);
        return ResponseEntity.status(status).body(apiError);
    }
}
