package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
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
public class UrlExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e, HttpServletRequest request) {
        return createErrorResponse(e, HttpStatus.BAD_REQUEST, "Validation Error", request);
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e, HttpServletRequest request) {
        return createErrorResponse(e, HttpStatus.NOT_FOUND, "Not found URL", request);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e, HttpServletRequest request) {
        return createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, "Server side error", request);
    }

    private ErrorResponse createErrorResponse(Exception e, HttpStatus status, String error, HttpServletRequest request) {
        return new ErrorResponse(
                e.getMessage(),
                status.value(),
                error,
                request.getRequestURI()
        );
    }

}
