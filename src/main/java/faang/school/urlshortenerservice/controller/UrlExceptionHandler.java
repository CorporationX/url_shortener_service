package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e){
        log.error("DataValidationException: ",e);
        return new ErrorResponse(LocalDateTime.now(),400, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e){
        log.error("Runtime exception:",e);
        return new ErrorResponse(LocalDateTime.now(),500, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse defaultExceptionHandler(Exception e){
        log.error("Not specific exception",e);
        return new ErrorResponse(LocalDateTime.now(),500, e.getMessage());
    }
}
