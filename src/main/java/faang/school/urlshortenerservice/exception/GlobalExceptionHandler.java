package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DataUrlValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataUrlValidationException(DataUrlValidationException e) {
        return new ErrorResponse(e.getMessage());
    }
}