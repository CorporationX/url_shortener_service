package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.dto.ExceptionResponse;
import faang.school.urlshortenerservice.exception.CacheOperationException;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.exception.SchedulerException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlShorteningException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUrlNotFoundException(
            UrlNotFoundException ex,
            HttpServletRequest request) {
        log.warn("URL not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HashGenerationException.class)
    public ResponseEntity<ExceptionResponse> handleHashGenerationException(
            HashGenerationException ex,
            HttpServletRequest request) {
        log.error("Hash generation error: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to generate or retrieve hash. Please try again later.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(CacheOperationException.class)
    public ResponseEntity<ExceptionResponse> handleCacheOperationException(
            CacheOperationException ex,
            HttpServletRequest request) {
        log.error("Cache operation error: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to perform cache operation. Please try again later.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Request body validation error: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        log.warn("Path variable validation error: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("Invalid configuration value: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid configuration: " + ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        log.error("Internal server error", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal server error occurred. Please try again later.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllExceptions(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UrlShorteningException.class)
    public ResponseEntity<ExceptionResponse> handleUrlShorteningException(
            UrlShorteningException ex,
            HttpServletRequest request) {
        log.error("URL shortening error: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create short URL. Please try again later.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(SchedulerException.class)
    public ResponseEntity<ExceptionResponse> handleSchedulerException(
            SchedulerException ex,
            HttpServletRequest request) {
        log.error("Scheduler error: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal scheduler error occurred. Please try again later.",
                request.getRequestURI()
        );
    }

    private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, String message, String path) {
        ExceptionResponse response = ExceptionResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
