package faang.school.urlshortenerservice.controller.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class UrlExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.value(),
            getErrorAndLog("exception.entity_not_found", e),
            e.getMessage(),
            LocalDateTime.now());
    }

    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidationException(ValidationException e, HttpServletRequest request) {
        return buildErrorResponse(
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.value(),
            getErrorAndLog("error.validation_exception", e),
            e.getMessage(),
            LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e, HttpServletRequest request) {
        return buildErrorResponse(
            request.getRequestURI(),
            500,
            getErrorAndLog("error.exception", e),
            e.getMessage(),
            LocalDateTime.now());
    }

    private String getErrorAndLog(String msgProperty, Exception e) {
        String error = messageSource.getMessage(msgProperty, null, LocaleContextHolder.getLocale());
        log.error(error, e);
        return error;
    }

    private ErrorResponse buildErrorResponse(String url, int status, String error,
                                             String message, LocalDateTime timestamp) {
        return ErrorResponse.builder()
            .url(url)
            .status(status)
            .error(error)
            .message(message)
            .timestamp(timestamp)
            .build();
    }
}
