package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.UrlExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError ->
                                fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Validation error"
                ));

        log.warn("Validation failed: {}", errors);
        return buildProblemDetailResponse(HttpStatus.BAD_REQUEST, "Validation failed", Map.of("errors", errors));
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(UrlExpiredException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return buildProblemDetailResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(Exception e) {
        log.error("Database error occurred: {}", e.getMessage(), e);
        return buildProblemDetailResponse(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ResponseEntity<ProblemDetail> buildProblemDetailResponse(HttpStatus status, String detail) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    private ResponseEntity<ProblemDetail> buildProblemDetailResponse(HttpStatus status, String detail, Map<String, Object> properties) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        properties.forEach(problemDetail::setProperty);
        return ResponseEntity.status(status).body(problemDetail);
    }
}
