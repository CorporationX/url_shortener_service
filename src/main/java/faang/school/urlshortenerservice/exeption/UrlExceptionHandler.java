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
    @ExceptionHandler(BadUrl.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadUrlException(BadUrl e) {
        return buildResponse(e);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        return buildResponse(e);
    }

    private ErrorResponse buildResponse(Exception e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .error(e.getClass().getSimpleName())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
