package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HashException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleHashException(HashException e) {
        return e.getMessage();
    }
}
