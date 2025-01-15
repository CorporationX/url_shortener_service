package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.HashRetrievalException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.warn("URL not found: {}", ex.getMessage());
        return ResponseEntity.status(404)
                .body(new ErrorResponse("URL Not Found", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(HashRetrievalException.class)
    public ResponseEntity<ErrorResponse> handleHashRetrievalException(HashRetrievalException ex) {
        log.error("Hash retrieval error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(new ErrorResponse("Hash Retrieval Error", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(CacheException.class)
    public ResponseEntity<ErrorResponse> handleCacheException(CacheException ex) {
        log.error("Cache error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(new ErrorResponse("Cache Error", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(new ErrorResponse("Internal Server Error", "An unexpected error occurred", LocalDateTime.now()));
    }
}
