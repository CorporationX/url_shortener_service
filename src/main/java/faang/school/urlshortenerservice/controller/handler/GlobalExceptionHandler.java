package faang.school.urlshortenerservice.controller.handler;

import faang.school.urlshortenerservice.dto.ErrorModel;
import faang.school.urlshortenerservice.exceptions.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${service.name}")
    private String serviceName;

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorModel handleArgumentException(UrlNotFoundException ex) {
        log.error("Url not found", ex);
        return createError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handleArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception", ex);
        return createError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorModel handleException(Exception ex) {
        log.error("Internal server error exception", ex);
        return createError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ErrorModel createError(String message, int statusCode) {
        return new ErrorModel(message, statusCode, serviceName);
    }
}
