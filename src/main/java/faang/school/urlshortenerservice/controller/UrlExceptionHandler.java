package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ExceptionDto;
import faang.school.urlshortenerservice.exception.UrlNotValid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {
    @ExceptionHandler(UrlNotValid.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleUrlNotExistException(UrlNotValid e, HttpServletRequest request) {
        ExceptionDto dto = buildDto(e, request);
        log.error(dto.getMessage());
        return dto;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        ExceptionDto dto = buildDto(e, request);
        log.error(dto.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return dto;
    }

    private ExceptionDto buildDto(RuntimeException e, HttpServletRequest request) {
        return new ExceptionDto(
                e.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );
    }
}
