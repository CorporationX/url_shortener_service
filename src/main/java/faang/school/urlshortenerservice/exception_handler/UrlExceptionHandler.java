package faang.school.urlshortenerservice.exception_handler;

import faang.school.urlshortenerservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleDataValidationException(DataValidationException e) {
        log.warn("Bad request: {}", e.getMessage());
        return getProblemDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleDataValidationException(EntityNotFoundException e) {
        log.warn("Bad request: {}", e.getMessage());
        return getProblemDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleRuntimeException(RuntimeException e) {
        log.error("Unexpected error occurred: {}", e.getMessage());
        return getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage());
        return getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ProblemDetail getProblemDetail(HttpStatus status, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setProperty("timeStamp", LocalDateTime.now());
        return problemDetail;
    }

}

