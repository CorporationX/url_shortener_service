package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.error.ErrorResponse;
import faang.school.urlshortenerservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        logException(exception, HttpStatus.NOT_FOUND);
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataValidationException.class)
    public ErrorResponse handleDataValidationException(DataValidationException exception) {
        logException(exception, HttpStatus.BAD_REQUEST);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException exception) {
        logException(exception, HttpStatus.INTERNAL_SERVER_ERROR);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();

        logException(exception, HttpStatus.BAD_REQUEST);
        exception.getBindingResult().getAllErrors()
                .forEach(error -> validationErrors.put(((FieldError) error).getField(), error.getDefaultMessage()));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed for fields: " + validationErrors);
    }

    private void logException(Exception exception, HttpStatus status) {
        log.error("Exception: {} | Status Code: {} | Message: {}",
                exception.getClass().getSimpleName(), status.value(), exception.getMessage(), exception);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .code(status.value())
                .message(message)
                .build();
    }
}
