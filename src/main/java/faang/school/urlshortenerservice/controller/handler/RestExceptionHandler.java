package faang.school.urlshortenerservice.controller.handler;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.HashNotExistException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        log.info("MethodArgumentNotValidException: {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return buildErrorResponse(ErrorCode.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(HashNotExistException.class)
    @ResponseStatus(HttpStatus.TOO_EARLY)
    public ErrorResponse handleValidationException(HashNotExistException ex) {
        log.info("HashNotExistException: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.TOO_EARLY, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(IllegalArgumentException ex) {
        log.info("IllegalArgumentException: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationException(EntityNotFoundException ex) {
        log.info("EntityNotFoundException: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
    }


    private ErrorResponse buildErrorResponse(ErrorCode errorCode, String errorMessage) {
        return new ErrorResponse(LocalDateTime.now(),
                errorCode.getCode().value(),
                errorCode.getMessage(), errorMessage);
    }
}
