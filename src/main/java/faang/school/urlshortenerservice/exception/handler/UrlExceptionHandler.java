package faang.school.urlshortenerservice.exception.handler;


import faang.school.urlshortenerservice.builder.ErrorResponseBuilder;
import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static faang.school.urlshortenerservice.message.ErrorMessage.INVALID_URL;
import static faang.school.urlshortenerservice.message.ErrorMessage.UNEXPECTED_ERROR;
import static faang.school.urlshortenerservice.message.ErrorMessage.URL_NOT_FOUND;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class UrlExceptionHandler {
    private final ErrorResponseBuilder errorResponseBuilder;

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException e, HttpServletRequest request) {
        log.error(INVALID_URL);
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder.build(e.getMessage(), HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException e, HttpServletRequest request) {
        log.error(URL_NOT_FOUND);
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder.build(e.getMessage(), HttpStatus.NOT_FOUND, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error(UNEXPECTED_ERROR);
        return ResponseEntity.badRequest()
                .body(errorResponseBuilder.build(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request));
    }
}