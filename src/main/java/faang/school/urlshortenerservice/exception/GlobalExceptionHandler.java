package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ApiErrorDto;
import faang.school.urlshortenerservice.dto.FieldErrorDetail;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import faang.school.urlshortenerservice.dto.ErrorDto;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleDataValidationException(DataValidationException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto("Data validation exception", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto("Entity not found exception", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());
        return new ErrorDto("IllegalArgumentException", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<FieldErrorDetail> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ApiErrorDto("Validation failed", validationErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleConstraintViolationException(ConstraintViolationException ex) {

        log.warn("Constraint violation exception: {}", ex.getMessage(), ex);

        List<FieldErrorDetail> errors = ex.getConstraintViolations().stream()
                .map(violation -> {
                    log.warn("Constraint violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
                    return new FieldErrorDetail(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    );
                })
                .collect(Collectors.toList());

        return new ApiErrorDto("Constraint violation", errors);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleThrowable(Throwable t) {
        log.error(t.getMessage(), t);
        return new ErrorDto("Interaction failure", t.getMessage());
    }
}
