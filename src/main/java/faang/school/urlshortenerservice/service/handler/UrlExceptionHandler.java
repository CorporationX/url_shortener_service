package faang.school.urlshortenerservice.service.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Locale;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class UrlExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        String error = getErrorAndLog("error.entity_not_found", e);
        return buildErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), error, e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleCustomJsonProcessingException(Exception e, HttpServletRequest request) {
        String error = getErrorAndLog("error.exception", e);
        return buildErrorResponse(request.getRequestURI(), 500, error, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleCustomJsonProcessingException(ValidationException e, HttpServletRequest request) {
        String error = getErrorAndLog("error.validation_exception", e);
        return buildErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), error, e.getMessage(),
                LocalDateTime.now());
    }

    private String getErrorAndLog(String msgProperty, Exception e) {
        String error = messageSource.getMessage(msgProperty, null, Locale.getDefault());
        log.error(error, e);
        return error;
    }

    private ErrorResponse buildErrorResponse(String url, int status, String error, String message, LocalDateTime timestamp) {
        return ErrorResponse.builder()
                .url(url)
                .status(status)
                .error(error)
                .message(message)
                .timestamp(timestamp)
                .build();
    }
}
