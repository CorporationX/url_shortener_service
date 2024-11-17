package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.error.ErrorResponseDto;
import faang.school.urlshortenerservice.dto.error.ErrorType;
import faang.school.urlshortenerservice.exceptions.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class UrlExceptionsHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDataValidationException(DataValidationException e, HttpServletRequest request) {
        return createErrorResponse(ErrorType.VALIDATION_ERROR, e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        return createErrorResponse(ErrorType.NOT_FOUND, e.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        return createErrorResponse(ErrorType.ILLEGAL_STATE, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleValidationExceptions(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ErrorResponseDto(
                ErrorType.VALIDATION_ERROR.getMessage(),
                "Validation failed for one or more fields",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e, HttpServletRequest request) {
        return createErrorResponse(ErrorType.EXTERNAL_SERVICE_ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ErrorResponseDto createErrorResponse(ErrorType errorType, String message, HttpStatus status, HttpServletRequest request) {
        log.error("{}: {}", errorType.getMessage(), message);
        return new ErrorResponseDto(
                errorType.getMessage(),
                message,
                status.value(),
                request.getRequestURI()
        );
    }
}
