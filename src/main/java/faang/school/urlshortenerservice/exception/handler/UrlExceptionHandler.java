package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.dto.ExceptionDto;
import faang.school.urlshortenerservice.exception.OriginalUrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlNotRecognizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class UrlExceptionHandler {
    @Value("${server.exceptions.default-message}")
    private String defaultMessage;

    @ExceptionHandler(value = {UrlNotRecognizedException.class, OriginalUrlNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionDto urlNotRecognisedExceptionHandler(UrlNotRecognizedException e) {
        log.error(e.getMessage(), e);
        return new ExceptionDto(e.getMessage(), 1);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ExceptionDto exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return new ExceptionDto(defaultMessage, 2);
    }
}
