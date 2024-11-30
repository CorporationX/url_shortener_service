package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.IncorrectUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(IncorrectUrl.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidateException(IncorrectUrl ex) {
        log.error(ex.toString());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception ex) {
        log.error(ex.toString());
        return Map.of("error", ex.getMessage());
    }
}
