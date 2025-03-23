package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.BadUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {
    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUrlNotFoundException(UrlNotFoundException e) {
        log.info("Handled url not found exception message:{} \ncause: {}", e.getMessage(), e.getCause());
        return e.getMessage();
    }

    @ExceptionHandler(BadUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadUrlException(BadUrlException e) {
        log.info("Handled bad url exception message:{} \ncause: {}", e.getMessage(), e.getCause());
        return e.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUrlNotFoundException(RuntimeException e) {
        log.info("Some exception occured message:{} \ncause: {}", e.getMessage(), e.getCause());
        return e.getMessage();
    }
}
