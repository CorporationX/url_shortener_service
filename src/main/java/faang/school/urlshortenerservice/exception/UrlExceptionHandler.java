package faang.school.urlshortenerservice.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Component
public class UrlExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST, "Bad request", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST, "Bad request", e.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST, "Invalid argument", e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e.getMessage());
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest rq, HttpStatus status, String error, String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(rq.getRequestURI())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
    }
}
