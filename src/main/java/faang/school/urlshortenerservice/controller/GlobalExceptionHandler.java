
package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ApiError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handle(EntityNotFoundException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handle(ConstraintViolationException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handle(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handle(MethodArgumentNotValidException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle(Exception ex, HttpServletRequest req) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    private ResponseEntity<ApiError> buildErrorResponse(Exception ex, HttpStatus status, HttpServletRequest request) {
        String fullPath = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null && !query.isBlank()) {
            fullPath += "?" + query;
        }

        ApiError error = new ApiError(
                ex.getMessage(),
                status.value(),
                request.getMethod(),
                fullPath,
                Instant.now().toString()
        );

        log.error("Handled Exception: [{}]: {}", status, ex.getMessage(), ex);
        return ResponseEntity.status(status).body(error);
    }
}

