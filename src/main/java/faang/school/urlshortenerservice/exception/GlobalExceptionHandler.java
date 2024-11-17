package faang.school.urlshortenerservice.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<FieldError> errors = exception.getFieldErrors();
        ErrorResponse errorResponse = ErrorResponse.builder().errors(new HashMap<>()).build();
        errors.forEach(
                error -> errorResponse.getErrors().put(error.getField(), error.getDefaultMessage())
        );
        return errorResponse;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFound(EntityNotFoundException exception) {
        return ErrorResponse.builder().message(exception.getMessage()).build();
    }
}
