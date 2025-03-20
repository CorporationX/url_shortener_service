package faang.school.urlshortenerservice.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse exception(final MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        FieldError::getDefaultMessage, (r1, r2) -> r1));
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                errors
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MissingRequestCookieException e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Missing required cookie",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final BusinessException e) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "Business Rule Violation",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(final EntityAlreadyExistException e) {
        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "Entity already exist",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final EntityNotFoundException e) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "Entity not found",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                e.getMessage()
        );
    }
}
