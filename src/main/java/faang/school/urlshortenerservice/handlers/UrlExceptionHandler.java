package faang.school.urlshortenerservice.handlers;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException: ", exception);
        return ResponseEntity.badRequest().body(
                exception.getFieldErrors().stream()
                        .map(fieldError -> Map.of(
                                fieldError, String.format("%s. Actual value: %s", fieldError.getDefaultMessage(),
                                        fieldError.getRejectedValue())
                        ))
                        .toList()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.error("EntityNotFoundException: ", exception);
        return ResponseEntity.badRequest().body(
                new HashMap<>() {{
                    put("message", "Entity not found");
                    put("details", exception.getMessage());
                }}
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exception) {
        log.error("Some exception: ", exception);
        return ResponseEntity.internalServerError().body(
                exception.getMessage()
        );
    }
}
