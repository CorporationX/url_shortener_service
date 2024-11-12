package faang.school.urlshortenerservice.exeption.handler;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @Value("${spring.application.name}")
    private String serviceName;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage())
                                .orElse("Invalid input for field " + error.getField())
                ));

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validation error")
                .details(details)
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Error 404: {}", ex.getMessage());

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal server error. Please try again later.")
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllOtherExceptions(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An error occurred. Please contact support.")
                .build();
    }
}