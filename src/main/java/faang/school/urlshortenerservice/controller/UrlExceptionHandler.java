package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ExceptionDto;
import faang.school.urlshortenerservice.exception.UrlNotExistException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UrlExceptionHandler {
    @ExceptionHandler(UrlNotExistException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleUrlNotExistException(UrlNotExistException e, HttpServletRequest request) {
        return new ExceptionDto(
                e.getMessage(),
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }
}
