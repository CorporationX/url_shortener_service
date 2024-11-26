package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.UrlExpiredException;
import faang.school.urlshortenerservice.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<?> handleValidationException(UrlExpiredException e) {
        return handleException(e, NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        return handleException(e, BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleRuntimeException(Exception e) {
        return handleException(e, INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> handleException(Exception e, HttpStatus status) {
        String errorMessage = e.getMessage();

        log.error(errorMessage, e);
        return new ResponseEntity<>(errorMessage, status);
    }
}
