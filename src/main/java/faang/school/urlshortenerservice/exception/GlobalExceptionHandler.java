package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.responses.ConstraintErrorResponse;
import faang.school.urlshortenerservice.dto.responses.Violation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import faang.school.urlshortenerservice.dto.responses.ErrorResponse;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ConstraintErrorResponse handleValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
            .map(
                violation -> new Violation(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
                )
            )
            .toList();
        log.error(e.getMessage(), e);
        return new ConstraintErrorResponse(violations);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}
