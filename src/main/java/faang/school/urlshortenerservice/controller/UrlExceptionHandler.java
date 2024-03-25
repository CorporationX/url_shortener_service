package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(ServerException.class)
    protected final ResponseEntity<ErrorResponse> handleAllException(ServerException ex) {
        log.error("Internal Server Error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(getErrorResponse(ex));
    }

    @ExceptionHandler(DataValidationException.class)
    protected final ResponseEntity<Object> handleBadRequest(DataValidationException ex) {
        log.error("Bad request", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    private ErrorResponse getErrorResponse(ServerException ex) {
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }
}
