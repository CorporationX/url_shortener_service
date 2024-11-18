package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.FreeHashNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    private static final String SOMETHING_MESSAGE_ERROR = "Something went wrong";

    @Value("${server.name}")
    private String serviceName;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class, FreeHashNotFoundException.class})
    public ErrorResponse handleRuntimeException(RuntimeException exception) {
        String message = exception.getMessage();
        log.error(message, exception);

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.NOT_FOUND.value())
                .globalMessage(message)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityExistsException.class)
    public ErrorResponse handleEntityExistsException(PersistenceException exception) {
        String message = exception.getMessage();
        log.error(message, exception);

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .globalMessage(message)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataValidationException.class)
    public ErrorResponse handleDataValidationException(DataValidationException exception) {
        String message = exception.getMessage();
        log.error(message, exception);

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .globalMessage(message)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse(SOMETHING_MESSAGE_ERROR)
                ));
        log.error("Validation errors: {}", errors);

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorDetails(errors)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        String message = exception.getMessage();
        log.error(message, exception);

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .globalMessage(message)
                .build();
    }
}
