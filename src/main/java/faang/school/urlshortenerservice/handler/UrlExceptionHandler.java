package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.NoHashValueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {
    @ExceptionHandler(NoHashValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoHashValueException(NoHashValueException ex) {
        log.error("Caught an NoHashValueException", ex);
        return new ErrorResponse("No Hash Value provided", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException (RuntimeException ex) {
        log.error("Caught a Runtime Exception", ex);
        return new ErrorResponse("Runtime Exception caught", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIllegalStateException (IllegalStateException ex) {
        log.error("Caught an IllegalStateException", ex);
        return new ErrorResponse("IllegalStateException has been caught", ex.getMessage());
    }

}
