package faang.school.urlshortenerservice.handler;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springdoc.api.ErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleMethodEntityNotFoundException(EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleMethodDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorMessage> handleMethodFeignException(FeignException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }
}