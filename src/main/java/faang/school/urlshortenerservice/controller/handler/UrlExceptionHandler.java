package faang.school.urlshortenerservice.controller.handler;

import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ErrorResponseDto handleNotFound(EntityNotFoundException exception) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                ErrorReasons.getMessageFor(exception),
                exception.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("EntityNotFoundException was thrown, errorResponseDto: {}", errorResponseDto, exception);
        return errorResponseDto;
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequestExceptions(DataValidationException exception) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                ErrorReasons.getMessageFor(exception),
                exception.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("DataValidationException was thrown, errorResponseDto: {}", errorResponseDto, exception);
        return errorResponseDto;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception exception) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ErrorReasons.getMessageFor(exception),
                exception.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("Exception was thrown, errorResponseDto: {}", errorResponseDto, exception);
        return errorResponseDto;
    }
}
