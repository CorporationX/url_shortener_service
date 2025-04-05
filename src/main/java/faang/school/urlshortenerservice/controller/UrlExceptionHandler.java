package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.ErrorResponse;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UrlExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(UrlExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(IllegalArgumentException ex) {
        logger.warn("Validation error occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        logger.warn("URL not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Ошибка: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}