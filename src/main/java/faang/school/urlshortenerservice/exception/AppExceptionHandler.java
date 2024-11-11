package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        return handleException(e, BAD_REQUEST);
    }

    private ResponseEntity<?> handleException(Exception e, HttpStatus status) {
        String errorMessage = e.getMessage();

        log.error(errorMessage, e);
        return new ResponseEntity<>(errorMessage, status);
    }
}
