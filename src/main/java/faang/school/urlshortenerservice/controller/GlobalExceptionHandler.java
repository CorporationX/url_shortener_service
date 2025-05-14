package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.NoAvailableHashInCacheException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoAvailableHashInCacheException.class)
    public ResponseEntity<ErrorResponse> handleNoAvailableHashInCacheException(
            NoAvailableHashInCacheException ex
    ) {
        log.error("No available hash in cache: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "NO_AVAILABLE_HASH",
                ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(
            UrlNotFoundException ex
    ) {
        log.error("URL not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "URL_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join("; ", errors);
        log.error("Validation error: {}", errorMessage);

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_ERROR",
                errorMessage,
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ) {
        log.error("Data integrity violation: {}", ex.getMessage());
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        ErrorResponse errorResponse = new ErrorResponse(
                "DATA_INTEGRITY_ERROR",
                "Database error: " + rootMsg,
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex
    ) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}