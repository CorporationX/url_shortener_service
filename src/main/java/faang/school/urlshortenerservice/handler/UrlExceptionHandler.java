package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(Exception exception) {
        log.error("Internal server error: {}", exception.getMessage());
        LocalDateTime timestamp = LocalDateTime.now();
        return new ErrorResponse(timestamp, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException exception) {
        log.error("Data validation error: {}", exception.getMessage());
        LocalDateTime timestamp = LocalDateTime.now();
        return new ErrorResponse(timestamp, HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException: {}", exception.getMessage());
        return exception.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllExceptions(Exception exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", exception.getMessage() != null ? exception.getMessage() : "Unknown error");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}