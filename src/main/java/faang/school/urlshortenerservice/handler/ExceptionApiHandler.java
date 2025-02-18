package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.CacheEmptyException;
import faang.school.urlshortenerservice.exception.InternalValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(InternalValidationException.class)
    public ResponseEntity<ErrorMessage> handleInternalValidationException(InternalValidationException exception) {
        log.error(String.format("Error validation, message : %s", exception));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.error(String.format("Error entity not found, message : %s", exception));
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(CacheEmptyException.class)
    public ResponseEntity<ErrorMessage> handleCacheEmptyException(CacheEmptyException exception) {
        log.error(String.format("Error cache empty exception, message : %s", exception));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception exception) {
        log.error(String.format("Error Exception.class, message : %s", exception));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage("Internal server error. Our developers are working."));
    }
}