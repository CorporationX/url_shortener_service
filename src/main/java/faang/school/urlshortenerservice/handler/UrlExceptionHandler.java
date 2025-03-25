package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.exception.UrlNotDetectedException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(HashNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(@NotNull HashNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(@NotNull IllegalArgumentException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(@NotNull UrlNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(UrlNotDetectedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(@NotNull UrlNotDetectedException exception) {
        return new ErrorResponse(exception.getMessage());
    }

}
