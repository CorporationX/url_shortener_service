package faang.school.urlshortenerservice.exception.handler;


import faang.school.urlshortenerservice.builder.ErrorResponseBuilder;
import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class UrlExceptionHandler {
    private final ErrorResponseBuilder errorResponseBuilder;

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException e, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder.build(e.getMessage(), HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder.build(e.getMessage(), HttpStatus.NOT_FOUND, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder.build(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request));
    }
}