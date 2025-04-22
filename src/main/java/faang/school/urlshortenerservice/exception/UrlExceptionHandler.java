package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Object> handleUrlNotFoundException(UrlNotFoundException ex) {
        String message = ex.getMessage();
        log.error("Url not found: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(BatchSizeException.class)
    public ResponseEntity<Object> handleBatchSizeException(BatchSizeException ex) {
        log.error(ex.getMessage());
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.error("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .errors(errors)
                        .message("Validation failed")
                        .build());
    }

    private ResponseEntity<Object> buildErrorResponseEntity(HttpStatus status, String message) {
        ErrorResponse apiError = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .build();
        return ResponseEntity.status(status).body(apiError);
    }
}
