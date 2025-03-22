package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UrlExceptionHandler {

  public static final String DEFAULT_SERVICE_NAME = "post-service";

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleRuntimeException(RuntimeException e) {
    return new ErrorResponse(DEFAULT_SERVICE_NAME, HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR", e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(EntityNotFoundException.class)
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
    return new ErrorResponse(DEFAULT_SERVICE_NAME, HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND",
        e.getMessage() != null ? e.getMessage() : "Entity not found");
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
    return new ErrorResponse(DEFAULT_SERVICE_NAME, HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT",
        e.getMessage() != null ? e.getMessage() : "Invalid argument");
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

    return new ErrorResponse(DEFAULT_SERVICE_NAME, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
        message);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
    String errorMessage = e.getConstraintViolations()
        .stream()
        .map(violation -> String.format("Field '%s': %s", violation.getPropertyPath(),
            violation.getMessage()))
        .collect(Collectors.joining("; "));

    return new ErrorResponse(DEFAULT_SERVICE_NAME, HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION",
        errorMessage);
  }

  @ExceptionHandler(UrlNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handlePostBadRequestException(UrlNotFoundException e) {
    return new ErrorResponse(e.getServiceName(), HttpStatus.NOT_FOUND,
        "URL_SHORTENER_NOT_FOUND",
        e.getMessage());
  }

  @ExceptionHandler(FeignException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleFeignException(FeignException e) {
    String message = e.contentUTF8();
    return new ErrorResponse(DEFAULT_SERVICE_NAME, HttpStatus.BAD_REQUEST, "FEIGN_CLIENT_ERROR",
        message != null && !message.isEmpty() ? message : "Feign client error");
  }
}