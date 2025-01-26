package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.TemporarilyUnavailableException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private final static String URL_NOT_FOUND = "URL not found exception: {}";
    private final static String DATABASE_ERROR_SHORT_MESSAGE = "A database error occurred: {}";
    private final static String UNEXPECTED_ERROR_SHORT_MESSAGE = "An unexpected error occurred: {}";
    private final static String DATABASE_ERROR = "A database error occurred. Please try again later.";
    private final static String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again later.";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        log.error("Validation Error: {}", errorMessage);
        return buildResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.error(URL_NOT_FOUND, ex.getMessage(), ex);
        return buildResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        log.error(DATABASE_ERROR_SHORT_MESSAGE, ex.getMessage(), ex);
        return buildResponseEntity(DATABASE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleTemporarilyUnavailableException(TemporarilyUnavailableException ex) {
        log.error(UNEXPECTED_ERROR_SHORT_MESSAGE, ex.getMessage(), ex);
        return buildResponseEntity(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        log.error(UNEXPECTED_ERROR_SHORT_MESSAGE, ex.getMessage(), ex);
        return buildResponseEntity(UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> buildResponseEntity(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(message);
    }
}
