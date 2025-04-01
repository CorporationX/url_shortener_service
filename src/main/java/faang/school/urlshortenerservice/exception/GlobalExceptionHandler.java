package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            NoSuchShortUrlException.class, HttpStatus.NOT_FOUND,
            IllegalArgumentException.class, HttpStatus.BAD_REQUEST
    );

    private static final Map<Class<? extends Exception>, String> EXCEPTION_DEFAULT_MESSAGE_MAP = Map.of(
            MethodArgumentNotValidException.class, "Incorrect url. Please try again.",
            NoSuchShortUrlException.class, "Incorrect short url. Please try again.",
            IllegalArgumentException.class, "Requested time is greater than we can save"
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = getErrorMessage(ex);

        log.error("Exception caught: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        if (ex instanceof MethodArgumentNotValidException validationEx) {
            return ResponseEntity.status(status).body(buildValidationExMap(validationEx));
        }

        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    private Map<String, String> buildValidationExMap(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(err -> {
                    if (err instanceof FieldError fieldError) {
                        errors.put(fieldError.getField(), err.getDefaultMessage());
                    }
                });
        return errors;
    }

    private String getErrorMessage(Exception ex) {
        if (EXCEPTION_DEFAULT_MESSAGE_MAP.containsKey(ex.getClass())) {
            return EXCEPTION_DEFAULT_MESSAGE_MAP.get(ex.getClass());
        }
        return ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.";
    }
}
