package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.ApiException;
import faang.school.urlshortenerservice.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class UrlExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        HttpStatus status = INTERNAL_SERVER_ERROR;
        String message = "Internal Server Error";
        Map<String, String> errors = new HashMap<>();

        if (exception instanceof BindException) {
            status = BAD_REQUEST;
            message = "Ошибка валидации";
            errors = ((BindException) exception).getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));
        } else if (exception instanceof ApiException) {
            status = ((ApiException) exception).getHttpStatus();
            message = exception.getMessage();
        }

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .message(message)
                        .errors(errors)
                        .build());
    }
}
