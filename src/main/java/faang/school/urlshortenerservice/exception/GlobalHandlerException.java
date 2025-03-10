package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ErrorField;
import faang.school.urlshortenerservice.dto.ErrorResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception, WebRequest request) {
        return buildErrorMessage(exception, request);
    }

    @ExceptionHandler({RedirectException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRedirectException(RedirectException exception, WebRequest request) {
        return buildErrorMessage(exception, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception, WebRequest request) {
        List<ErrorField> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorField(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .message("Validation failed")
                .path(getPath(request))
                .details(validationErrors)
                .build();
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDataAccessException(DataAccessException exception, WebRequest request) {
        return buildErrorMessage(exception, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception exception, WebRequest request) {
        return buildErrorMessage(exception, request);
    }

    private ErrorResponse buildErrorMessage(Exception exception, WebRequest request) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .path(getPath(request))
                .build();
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
