package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.Error;
import faang.school.urlshortenerservice.exception.BusinessException;
import faang.school.urlshortenerservice.exception.NoUrlException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Error> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(fromBusinessException(e));
    }

    @ExceptionHandler(NoUrlException.class)
    public ResponseEntity<?> exception(Exception e) {
        return ResponseEntity.status(HttpStatus.valueOf(404))
                .body(e.getMessage());
    }

    private Error fromBusinessException(BusinessException e) {
        return new Error(
                e.getCode(),
                e.getMessage()
        );
    }
}
