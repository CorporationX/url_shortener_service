package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import faang.school.urlshortenerservice.dto.exception.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return build(HttpStatus.BAD_REQUEST, "Validation error", message, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception,
                                                                        HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Bad Request",
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException exception,
                                                                    HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND,
                "Url not found",
                exception.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception,
                                                         HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                exception.getMessage(),
                request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status,
                                                String error,
                                                String message,
                                                String path) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}

