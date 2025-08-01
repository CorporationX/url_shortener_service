package faang.school.urlshortenerservice.controller.handler;

import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import faang.school.urlshortenerservice.exception.NoHashAvailableException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                "The required entity was not found.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("Handled EntityNotFoundException - error response: {}, exception: ", errorResponseDto, e);
        return errorResponseDto;
    }

    @ExceptionHandler(NoHashAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(NoHashAvailableException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                "No url available for the given hash.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("Handled NoHashAvailableException - error response: {}, exception: ", errorResponseDto, e);
        return errorResponseDto;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something went wrong.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("Handled generic Exception - error response: {}, exception: ", errorResponseDto, e);
        return errorResponseDto;
    }
}
