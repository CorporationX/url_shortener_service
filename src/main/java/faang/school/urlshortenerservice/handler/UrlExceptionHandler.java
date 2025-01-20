package faang.school.urlshortenerservice.handler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation errors: {}", errors);
        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining(","));
        ErrorResponse errorResponse = getErrorResponse(request, HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({
            EntityExistsException.class,
            EntityNotFoundException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptions(Exception ex, HttpServletRequest request) {
        String errorMessage = ex.getMessage();
        log.error(ex.getClass() + ": " + errorMessage, ex);
        ErrorResponse errorResponse = getErrorResponse(request, HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private static ErrorResponse getErrorResponse(HttpServletRequest request, HttpStatus status, String
            errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(errorMessage);
        errorResponse.setPath(request.getRequestURI());
        return errorResponse;
    }
}
