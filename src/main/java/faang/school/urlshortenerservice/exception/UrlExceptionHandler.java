package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        log.error("Error: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        log.error("Internal system error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal system error. Please try again later.");
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleValidationException(Exception ex) {
        log.error("Validation error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Data validation error: " + ex.getMessage());
    }
}
