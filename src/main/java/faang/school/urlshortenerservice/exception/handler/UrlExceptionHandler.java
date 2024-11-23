package faang.school.urlshortenerservice.exception.handler;

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
    private static final String SOMETHING_MESSAGE_ERROR = "Something went wrong";

    @Value("${spring.application.name}")
    private String serviceName;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage())
                                .orElse(SOMETHING_MESSAGE_ERROR)
                ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .serviceName(serviceName)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .details(details)
                .build();
        log.error("Not Valid Exception: {}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        log.error("Not Found Exception: {}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleRuntimeException(Exception exception) {
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        log.error("Exception: {}", errorResponse);
        return errorResponse;
    }


    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .statusCode(status.value())
                .message(message)
                .build();
    }
}