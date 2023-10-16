package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.dto.Error;
import faang.school.urlshortenerservice.exception.BusinessException;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Error> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(fromBusinessException(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> body = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            body.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Object> handleInvalidUrlException(InvalidUrlException e) {
        Map<String, String> body = Map.of("message", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<Object> handleMalformedURLException(MalformedURLException e) {
        log.error("MalformedURLException occurred");
        Map<String, String> body = Map.of("message", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException occurred");
        Map<String, String> body = Map.of("message", processConstraintViolationException(e));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    private Error fromBusinessException(BusinessException e) {
        return new Error(
                e.getCode(),
                e.getMessage()
        );
    }

    private String processConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getSQLException().getMessage();

        String[] messages = errorMessage.split("\n");
        if (messages.length > 1) {
            return messages[1].trim();
        }
        return errorMessage;
    }
}
