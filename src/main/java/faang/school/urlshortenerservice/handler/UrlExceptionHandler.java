package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Component
@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(DataValidationException ex) {
        String message = ex.getMessage();
        log.error("EntityNotFoundException, {}", message, ex);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationException(UrlNotFoundException ex) {
        String message = ex.getMessage();
        log.error("UrlNotFoundException, {}", message, ex);
        return new ErrorResponse(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        log.error("RuntimeException, {}", message, ex);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGlobalException(Exception ex) {
        String message = ex.getMessage();
        log.error("GlobalException, {}", message, ex);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
