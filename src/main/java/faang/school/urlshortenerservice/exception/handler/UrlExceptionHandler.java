package faang.school.urlshortenerservice.exception.handler;


import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException e, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder(e.getMessage(), HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder(e.getMessage(), HttpStatus.NOT_FOUND, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request));
    }

    private ErrorResponse errorResponseBuilder(String message, HttpStatus status, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}