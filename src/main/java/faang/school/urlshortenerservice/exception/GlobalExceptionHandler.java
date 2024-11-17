package faang.school.urlshortenerservice.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorExceptions(Exception ex) {
        return getResponse(ex);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(ValidationException ex) {
        return getResponse(ex);
    }

    @ExceptionHandler(UrlNotfoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleUrlNotfoundExceptionExceptions(UrlNotfoundException ex) {
        return getResponse(ex);
    }

    private ErrorResponse getResponse(Exception ex) {
        log.error("Exception occurred: {}", ex.toString(), ex);
        return new ErrorResponse(ex.getMessage());
    }
}
