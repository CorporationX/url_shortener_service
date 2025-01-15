package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.HashRetrievalException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import org.hibernate.cache.CacheException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse("URL Not Found", ex.getMessage()));
    }

    @ExceptionHandler(HashRetrievalException.class)
    public ResponseEntity<ErrorResponse> handleHashRetrievalException(HashRetrievalException ex) {
        return ResponseEntity.status(500).body(new ErrorResponse("Hash Retrieval Error", ex.getMessage()));
    }

    @ExceptionHandler(CacheException.class)
    public ResponseEntity<ErrorResponse> handleCacheException(CacheException ex) {
        return ResponseEntity.status(500).body(new ErrorResponse("Cache Error", ex.getMessage()));
    }
}
