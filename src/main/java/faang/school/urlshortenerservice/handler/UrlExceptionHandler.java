package faang.school.urlshortenerservice.handler;

import jakarta.validation.ConstraintViolationException;
import liquibase.exception.DatabaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Component
@RestControllerAdvice
public class UrlExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleInternalServiceAll(Exception exception){
        Map<String, String> bodyException = new HashMap<>();
        String message = "Internal System Error";
        bodyException.put(message, exception.getClass().getName());
        return new ResponseEntity<>(bodyException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
            ConstraintViolationException.class,
            NullPointerException.class,
            NumberFormatException.class,
            IndexOutOfBoundsException.class
    })
    public ResponseEntity<Map<String,String>> handleBadRequest(Exception exception) {
        Map<String, String> bodyException = new HashMap<>();
        String message = "Bad argument request";
        bodyException.put(message, exception.getClass().getName());
        return new ResponseEntity<>(bodyException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            DatabaseException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Map<String,String>> handleInternalSpecific(Exception exception){
        Map<String, String> bodyException = new HashMap<>();
        String message = "Specific internal Exception";
        bodyException.put(message, exception.getClass().getName());
        return new ResponseEntity<>(bodyException, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
