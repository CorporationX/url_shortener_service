package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.ShortUrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.error("Validation errors: {}", errors, ex);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .code(VALIDATION_ERROR)
                        .message("Validation failed")
                        .details(errors)
                        .build());
    }

    @ExceptionHandler(ShortUrlNotFoundException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<ErrorResponse> handleShortUrlNotFoundException(ShortUrlNotFoundException e) {
        log.error("Short link is not found: {}", e.getMessage(), e);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .code(VALIDATION_ERROR)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleExceptions(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .code("INTERNAL_ERROR")
                        .message("An unexpected error occurred: %s".formatted(ex.getMessage())));
    }
}
