package faang.school.urlshortenerservice.exception.config;

import faang.school.urlshortenerservice.exception.common.DataValidationException;
import faang.school.urlshortenerservice.exception.common.PreConditionFailedException;
import faang.school.urlshortenerservice.exception.common.RecordNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> STATUS_STORAGE;

    static {
        STATUS_STORAGE = Map.of(
                DataValidationException.class, HttpStatus.BAD_REQUEST,
                MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
                ConstraintViolationException.class, HttpStatus.BAD_REQUEST,
                HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST,
                RecordNotFoundException.class, HttpStatus.NOT_FOUND,
                PreConditionFailedException.class, HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({
            DataValidationException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            PreConditionFailedException.class,
            RecordNotFoundException.class,
            HttpMessageNotReadableException.class,
            Exception.class
    })
    public <T extends Exception> ResponseEntity<ErrorResponse> handleException(T exception) {

        HttpStatus status = STATUS_STORAGE.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse error = new ErrorResponse(
                status.value(),
                extractGeneralMessage(exception),
                extractFieldErrors(exception)
        );

        log.error("Handled exception: {}", exception.getClass().getSimpleName(), exception);
        return new ResponseEntity<>(error, status);
    }

    private String extractGeneralMessage(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException) {
            return "Validation failed";
        }
        if (exception instanceof ConstraintViolationException) {
            return "Constraint violation";
        }
        return exception.getMessage();
    }

    private Map<String, String> extractFieldErrors(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException ex) {
            return ex.getBindingResult().getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fieldError -> fieldError.getDefaultMessage() == null
                                    ? exception.getMessage()
                                    : fieldError.getDefaultMessage(),
                            (m1, m2) -> m1 // если дубликаты — берем первый
                    ));
        }

        if (exception instanceof ConstraintViolationException ex) {
            return ex.getConstraintViolations().stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (m1, m2) -> m1
                    ));
        }

        return Map.of();
    }
}