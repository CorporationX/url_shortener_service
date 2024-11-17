package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.OriginalUrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlNotRecognizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UrlExceptionHandler {
    @Value("${server.exceptions.default-message}")
    private String defaultMessage;
    @ExceptionHandler(value = UrlNotRecognizedException.class)
    protected ResponseEntity<String> urlNotRecognisedExceptionHandler(UrlNotRecognizedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<String> runtimeExceptionHandler(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = OriginalUrlNotFoundException.class)
    protected ResponseEntity<String> originalUrlNotFoundExceptionHandler(OriginalUrlNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<String> exceptionHandler() {
        return new ResponseEntity<>(defaultMessage, HttpStatus.valueOf(500));
    }
}
