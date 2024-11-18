package faang.school.urlshortenerservice.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class UrlExceptionHandler {

    private static final String ERROR_KEY = "error";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + exception.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Map<String, String>> handleDataValidationException(DataValidationException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, exception.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, exception.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
