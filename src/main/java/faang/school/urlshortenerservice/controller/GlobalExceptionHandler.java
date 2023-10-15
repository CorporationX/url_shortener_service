package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.Error;
import faang.school.urlshortenerservice.exception.BusinessException;
import faang.school.urlshortenerservice.exception.url.InvalidUrlException;
import faang.school.urlshortenerservice.exception.url.ShortUrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Error> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(fromBusinessException(e));
    }

    private Error fromBusinessException(BusinessException e) {
        return new Error(
                e.getCode(),
                e.getMessage()
        );
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Error> handleInvalidUrlException(InvalidUrlException e) {
        Error error = new Error("Invalid Url", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(ShortUrlNotFoundException.class)
    public ResponseEntity<Error> handleShortUrlNotFoundException(ShortUrlNotFoundException e) {
        Error error = new Error("Short Url not found", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}