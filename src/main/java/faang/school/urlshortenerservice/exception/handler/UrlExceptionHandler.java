package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.UrlValidationException;
import faang.school.urlshortenerservice.exception.dto.ErrorResponseDto;
import faang.school.urlshortenerservice.exception.dto.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleEntityNotFoundException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("URL not found", e);
        return new ErrorResponseDto(
                ErrorType.BAD_REQUEST.getMessage(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UrlValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleUrlValidationException(UrlValidationException e, HttpServletRequest request) {
        log.error("Not a valid URL", e);
        return new ErrorResponseDto(
                ErrorType.URL_VALIDATION_ERROR.getMessage(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleValidationExceptions(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation Error", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ErrorResponseDto(
                ErrorType.VALIDATION_ERROR.getMessage(),
                "Validation failed for one or more fields",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                errors
        );
    }
}
