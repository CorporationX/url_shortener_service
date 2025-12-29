package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error: {}", message);
        
        return buildErrorResponse(
                "Validation failed: " + message,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        String message = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("Constraint violation: {}", message);
        
        return buildErrorResponse(
                "Validation failed: " + message,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        return buildErrorResponse(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(
            InvalidUrlException ex,
            HttpServletRequest request) {
        
        log.warn("Invalid URL: {}", ex.getMessage());
        
        return buildErrorResponse(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NoAvailableHashException.class)
    public ResponseEntity<ErrorResponse> handleNoAvailableHashException(
            NoAvailableHashException ex,
            HttpServletRequest request) {
        
        log.error("No available hash: {}", ex.getMessage());
        
        return buildErrorResponse(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler(UrlAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUrlAlreadyExistsException(
            UrlAlreadyExistsException ex,
            HttpServletRequest request) {
        
        log.warn("URL already exists: {}", ex.getMessage());
        
        return buildErrorResponse(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(
            UrlNotFoundException ex,
            HttpServletRequest request) {
        
        log.warn("URL not found: {}", ex.getMessage());
        
        return buildErrorResponse(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(
            RateLimitExceededException ex,
            HttpServletRequest request) {
        
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        
        return buildErrorResponse(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.TOO_MANY_REQUESTS
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        
        log.error("Internal server error: {}", ex.getMessage(), ex);
        
        return buildErrorResponse(
                "An internal error occurred: " + ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected error occurred", ex);
        
        return buildErrorResponse(
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            String path,
            HttpStatus status) {
        
        ErrorResponse errorResponse = new ErrorResponse(message, path);
        return ResponseEntity.status(status).body(errorResponse);
    }
}

