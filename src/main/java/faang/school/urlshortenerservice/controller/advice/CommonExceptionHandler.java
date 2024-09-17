package faang.school.urlshortenerservice.controller.advice;

import faang.school.urlshortenerservice.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ErrorMessage handleValidation(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ErrorMessage.builder()
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public ErrorMessage handleNotFound(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ErrorMessage.builder()
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ErrorMessage handleThrowable(Throwable e) {
        log.error(e.getMessage(), e);
        return ErrorMessage.builder()
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
