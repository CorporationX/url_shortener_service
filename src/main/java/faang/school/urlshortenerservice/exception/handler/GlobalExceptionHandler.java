package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleUrlNotFound(UrlNotFoundException ex) {
        log.error("URL not found: {}", ex.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument received: {}", ex.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleHashGeneration(HashGenerationException ex) {
        log.error("Hash generation failed: {}", ex.getMessage(), ex);
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }
}
