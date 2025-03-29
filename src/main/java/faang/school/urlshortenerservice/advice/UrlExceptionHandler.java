package faang.school.urlshortenerservice.advice;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<String> handleValidationError(Exception e) {
        log.warn("Validation failed: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid input data: " + e.getMessage());
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> handleUrlNotFound(UrlNotFoundException e) {
        log.warn("URL not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedError(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal error: " + e.getMessage());
    }
}