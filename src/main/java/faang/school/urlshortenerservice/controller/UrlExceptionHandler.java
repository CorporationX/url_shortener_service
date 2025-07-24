package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.ErrorResponse;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlValidateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class UrlExceptionHandler {
    @ExceptionHandler({UrlValidateException.class, ResponseStatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationException(RuntimeException e) {
        return getErrorResponse("handlerValidationException", e);
    }

    @ExceptionHandler({UrlNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerUrlFoundException(UrlNotFoundException e) {
        return getErrorResponse("handlerUrlFoundException", e);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerOtherException(RuntimeException e) {
        return getErrorResponse("handlerOtherException", e);
    }

    private ErrorResponse getErrorResponse(String exceptionLabel, Exception e) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    private ErrorResponse getErrorResponse(
            String exceptionLabel,
            String errorMessage,
            Map<String, String> detail,
            Exception e
    ) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponse(errorMessage, detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> detail = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
        String errorMessage = String.format("Validation failed with %s errors",
                e.getBindingResult().getFieldErrors().size());
        return getErrorResponse("handlerMethodArgumentNotValidException", errorMessage, detail, e);
    }
}
