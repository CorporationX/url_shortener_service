package faang.school.urlshortenerservice.controller.handler;

import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleUrlNotFound(UrlNotFoundException e) {
        log.error("UrlNotFoundException was thrown", e);
        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                ErrorMessage.URL_NOT_FOUND.errorMessage,
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException was thrown", e);
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                ErrorMessage.BAD_REQUEST.errorMessage,
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolation(ConstraintViolationException e) {
        log.error("ConstraintViolationException was thrown", e);
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                ErrorMessage.BAD_REQUEST.errorMessage,
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        log.error("Exception was thrown", e);
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ErrorMessage.INTERNAL_ERROR.errorMessage,
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }
}
