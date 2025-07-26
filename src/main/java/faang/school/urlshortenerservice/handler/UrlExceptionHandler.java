package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import faang.school.urlshortenerservice.enums.ErrorReason;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
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
    public ErrorResponseDto handleUrlNotFound(EntityNotFoundException e) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                ErrorReason.URL_NOT_FOUND,
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("URL not found error: {}", errorResponse, e);

        return errorResponse;
    }

    @ExceptionHandler(InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidUrl(InvalidUrlException e) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                ErrorReason.INVALID_URL,
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("Invalid URL error: {}", errorResponse, e);

        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ErrorReason.INTERNAL_ERROR,
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
        log.error("Some error occurred: {}", errorResponse, e);

        return errorResponse;
    }
}
