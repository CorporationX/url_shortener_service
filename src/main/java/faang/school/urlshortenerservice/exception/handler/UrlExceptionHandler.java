package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.BusinessValidationException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    // Business validation exceptions
    @ExceptionHandler(BusinessValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusinessValidationExceptions(BusinessValidationException e, HttpServletRequest request) {
        return ErrorResponseFactory.create(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e, HttpServletRequest request) {
        return ErrorResponseFactory.create(e, request, HttpStatus.NOT_FOUND);
    }

    // Spring, @Valid-exceptions (framework throws)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                               HttpServletRequest req) {
        return ErrorResponseFactory.create(e, req, HttpStatus.BAD_REQUEST);
    }

    // Hibernate, constraint violations (framework throws)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e,
                                                            HttpServletRequest req) {
        return ErrorResponseFactory.create(e, req, HttpStatus.BAD_REQUEST);
    }

    // System exceptions, unexpected exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSystemExceptions(Exception e, HttpServletRequest request) {
        log.error("{} occurred during {}-type request in URI: {} -> {}",
                getSimpleNameOfExceptionClass(e), safeMethod(request), safeUri(request), getExceptionMessage(e), e);

        return ErrorResponseFactory.create(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static String getSimpleNameOfExceptionClass(Throwable e) {
        return e.getClass().getSimpleName();
    }

    private static String getExceptionMessage(Throwable e) {
        return Optional.ofNullable(e.getMessage()).orElse("Some error");
    }

    private String safeMethod(HttpServletRequest req) {
        return Optional.ofNullable(req)
                .map(HttpServletRequest::getMethod)
                .orElse("N/A");
    }

    private String safeUri(HttpServletRequest req) {
        return Optional.ofNullable(req)
                .map(HttpServletRequest::getRequestURI)
                .orElse("N/A");
    }
}
