package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.exception.pojo.ResponseError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handlerEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException occurred: {}", e.getMessage(), e);
        return configureErrorResponse(e);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handlerValidationException(Exception e) {
        log.error("ValidationException occurred: {}", e.getMessage(), e);
        return configureErrorResponse(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handlerException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage(), e);
        return configureErrorResponse(e);
    }

    private ResponseError configureErrorResponse(Exception exception) {
        return ResponseError.builder()
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
