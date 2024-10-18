package faang.school.urlshortenerservice.service.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class UrlExceptionHandler {
  private final MessageSource messageSource;

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(BAD_REQUEST)
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
    String error = getErrorAndLog("error.entity_not_found", e);
    return buildErrorResponse(request.getRequestURI(), BAD_REQUEST.value(), error, e.getMessage(),
            LocalDateTime.now());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public ErrorResponse handleException(Exception e, HttpServletRequest request) {
    String error = getErrorAndLog("error.exception", e);
    return buildErrorResponse(request.getRequestURI(), INTERNAL_SERVER_ERROR.value(), error, e.getMessage(), LocalDateTime.now());
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(BAD_REQUEST)
  public ErrorResponse handleValidationException(ValidationException e, HttpServletRequest request) {
    String error = getErrorAndLog("error.validation_exception", e);
    return buildErrorResponse(request.getRequestURI(), BAD_REQUEST.value(), error, e.getMessage(),
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
