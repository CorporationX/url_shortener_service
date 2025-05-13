package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("Validation error: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.warn(ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({DatabaseAccessException.class, CacheRefillException.class,
            HashGenerationException.class})
    public ResponseEntity<Map<String, Object>> handleSystemExceptions(RuntimeException ex) {
        log.error("System error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of("status", status.value(), "error", status.getReasonPhrase(),
                        "message", message));
    }
}
