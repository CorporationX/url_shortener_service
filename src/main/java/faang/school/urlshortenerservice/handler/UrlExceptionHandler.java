package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.ExceptionResponse;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse ureHandlerException(UrlNotFoundException e) {
        log.warn("The url was not found in the system", e);
        return new ExceptionResponse(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse runTimeExceptionHandler(RuntimeException e) {
        log.error("Some exception in system", e);
        return new ExceptionResponse(e.getMessage(), LocalDateTime.now());
    }
}