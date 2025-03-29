package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {
    @Value("${spring.application.name}")
    private String serviceName;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HandlerMethod handlerMethod) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        printError(ex, handlerMethod);

        ErrorResponse errorResponse = createErrorResponse(handlerMethod, HttpStatus.BAD_REQUEST, "Validation failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex, HandlerMethod handlerMethod) {

        printError(ex, handlerMethod);

        ErrorResponse errorResponse = createErrorResponse(handlerMethod, HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HandlerMethod handlerMethod) {

        printError(ex, handlerMethod);

        ErrorResponse errorResponse = createErrorResponse(handlerMethod, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void printError(Exception ex, HandlerMethod handlerMethod) {
        log.error("Exception occurred in method: {}.{}",
                handlerMethod.getBeanType().getSimpleName(),
                handlerMethod.getMethod().getName(),
                ex);
    }

    private ErrorResponse createErrorResponse(HandlerMethod handlerMethod,
                                              HttpStatus status,
                                              String message,
                                              Map<String, Object> errors) {
        return new ErrorResponse(serviceName,
                status.value(), message, errors);
    }
}