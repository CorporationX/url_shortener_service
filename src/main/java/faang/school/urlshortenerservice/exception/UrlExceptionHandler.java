package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static faang.school.urlshortenerservice.exception.ErrorMessages.INTERNAL_SERVER_ERROR_MESSAGE;
import static faang.school.urlshortenerservice.exception.ErrorMessages.NOT_FOUND_ERROR_LOG_TEMPLATE;
import static faang.school.urlshortenerservice.exception.ErrorMessages.SYSTEM_ERROR_LOG_TEMPLATE;
import static faang.school.urlshortenerservice.exception.ErrorMessages.UNEXPECTED_ERROR_LOG_TEMPLATE;
import static faang.school.urlshortenerservice.exception.ErrorMessages.VALIDATION_ERROR_LOG_TEMPLATE;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn(VALIDATION_ERROR_LOG_TEMPLATE, message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.warn(NOT_FOUND_ERROR_LOG_TEMPLATE, ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({DatabaseAccessException.class, CacheRefillException.class,
            HashGenerationException.class})
    public ResponseEntity<Map<String, Object>> handleSystemExceptions(RuntimeException ex) {
        log.error(SYSTEM_ERROR_LOG_TEMPLATE, ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, WebRequest request) {
        log.error(UNEXPECTED_ERROR_LOG_TEMPLATE, ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of("status", status.value(), "error", status.getReasonPhrase(),
                        "message", message));
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUrlException(InvalidUrlException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
