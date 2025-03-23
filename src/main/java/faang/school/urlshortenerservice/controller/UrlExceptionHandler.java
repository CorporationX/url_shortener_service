package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class UrlExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleRuntimeException(RuntimeException e) {
    log.error("Unexpected internal error occurred: {}", e.getMessage(), e);

    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR", e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(EntityNotFoundException.class)
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
    log.info("Entity not found: {}", e.getMessage());

    return buildErrorResponse(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND",
        e.getMessage() != null ? e.getMessage() : "Entity not found");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    String message = e.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> {
          String field = ((FieldError) error).getField();
          String errorMessage = Objects.requireNonNullElse(error.getDefaultMessage(),
              "Invalid value");
          return field + ": " + errorMessage;
        })
        .collect(Collectors.joining("; "));
    log.info("Validation failed: {}", message);

    return buildErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
    String errorMessage = e.getConstraintViolations()
        .stream()
        .map(violation ->
            String.format("Field '%s': %s", violation.getPropertyPath(),
                violation.getMessage()))
        .collect(Collectors.joining("; "));
    log.info("Constraint violation: {}", errorMessage);

    return buildErrorResponse(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION",
        errorMessage);
  }

  @ExceptionHandler(UrlNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e) {
    log.info("URL not found: {}", e.getMessage());

    return buildErrorResponse(HttpStatus.NOT_FOUND, "URL_SHORTENER_NOT_FOUND",
        e.getMessage());
  }

  @ExceptionHandler(FeignException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleFeignException(FeignException e) {
    String message = e.contentUTF8();
    log.warn("Feign client error: {}", message);

    return buildErrorResponse(HttpStatus.BAD_REQUEST, "FEIGN_CLIENT_ERROR",
        message != null && !message.isEmpty() ? message : "Feign client error");
  }

  private ErrorResponse buildErrorResponse(HttpStatus status, String errorCode,
      String errorMessage) {
    return new ErrorResponse(status, errorCode, errorMessage);
  }
}