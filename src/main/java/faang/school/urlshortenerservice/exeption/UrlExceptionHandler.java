package faang.school.urlshortenerservice.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        return buildResponse(e);
    }

    private ErrorResponse buildResponse(Exception e) {
        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage(), e.getStackTrace().toString(), LocalDateTime.now());
    }
}
