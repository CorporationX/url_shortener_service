package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.error.ErrorResponse;
import faang.school.urlshortenerservice.error.ErrorType;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e, HttpServletRequest request) {
        ErrorResponse errorResponse = createErrorResponse(e, HttpStatus.BAD_REQUEST, ErrorType.VALIDATION_ERROR, request);
        log.error("An exception was thrown DataValidationException " + errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e, HttpServletRequest request) {
        ErrorResponse errorResponse = createErrorResponse(e, HttpStatus.NOT_FOUND, ErrorType.NOT_FOUND, request);
        log.error("An exception was thrown UrlNotFoundException " + errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e, HttpServletRequest request) {
        ErrorResponse errorResponse = createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.INTERNAL_SERVER_ERROR, request);
        log.error("An exception was thrown" + errorResponse);
        return errorResponse;
    }

    private ErrorResponse createErrorResponse(Exception e, HttpStatus status, ErrorType eType, HttpServletRequest request) {
        return new ErrorResponse(
                e.getMessage(),
                status.value(),
                eType.getMessage(),
                request.getRequestURI()
        );
    }

}
