package faang.school.urlshortenerservice.controller.handler;

import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.persistence.EntityNotFoundException;
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

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException e) {
        log.error("EntityNotFoundException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleUrlNotFound(UrlNotFoundException e) {
        log.error("UrlNotFoundException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                "Url was not found.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolation(ConstraintViolationException e) {
        log.error("ConstraintViolationException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        log.error("Exception with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something get wrong.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }
}
