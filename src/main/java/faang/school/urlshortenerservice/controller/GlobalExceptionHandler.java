package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.BadRequestException;
import faang.school.urlshortenerservice.exception.ForbiddenException;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
