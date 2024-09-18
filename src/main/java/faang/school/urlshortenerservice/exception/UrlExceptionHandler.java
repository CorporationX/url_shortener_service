package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler({InternalError.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleInternalError(RuntimeException e) {
        log.error("Internal error occurred: {}", e.getMessage(), e);
        return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal error occurred.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> errors.put(error.getDefaultMessage(), error.getDefaultMessage()));
        log.error("Validation errors occurred: {}", errors);
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGenericException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity not found: {}", e.getMessage());
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}