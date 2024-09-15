package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.ErrorResponse;
import faang.school.urlshortenerservice.exception.HashCacheException;
import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(HashCacheException.class)
    public ResponseEntity<Object> hashCacheException(HashCacheException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .details(e.getMessage())
                .message("Failed to retrieve hash")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> entityExistsException(EntityExistsException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .details(e.getMessage())
                .message("Entity already exists")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        String fieldName = fieldError.getField();
        String errorMessage = fieldError.getDefaultMessage();

        return new ResponseEntity<>(Map.of(fieldName, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .details(e.getMessage())
                .message("Internal Server Error")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
