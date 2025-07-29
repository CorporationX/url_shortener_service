package faang.school.urlshortenerservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logHandledException(e, request, errorId, HttpStatus.BAD_REQUEST);
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(e, request, HttpStatus.BAD_REQUEST, message, errorId);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        String message = e.getMessage() != null ? e.getMessage() : "Unexpected error occurred";
        logHandledException(e, request, errorId, HttpStatus.INTERNAL_SERVER_ERROR);
        return buildErrorResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR, message, errorId);
    }

    @ExceptionHandler({MalformedUrlException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(Exception e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logHandledException(e, request, errorId, HttpStatus.BAD_REQUEST);
        return buildErrorResponse(e, request, HttpStatus.BAD_REQUEST, e.getMessage(), errorId);
    }


    @ExceptionHandler({UrlNotFoundException.class, HashNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUrlNotFoundException(Exception e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logHandledException(e, request, errorId, HttpStatus.NOT_FOUND);
        return buildErrorResponse(e, request, HttpStatus.NOT_FOUND, e.getMessage(), errorId);
    }

    private void logHandledException(Exception e, HttpServletRequest request, String errorId, HttpStatus status) {
        log.error("Handled exception: ID={}, Type={}, Status={}, Message='{}', URL={}, Method={}",
                errorId,
                e.getClass().getSimpleName(),
                status.value(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                e
        );
    }

    private ErrorResponse buildErrorResponse(Exception e,
                                             HttpServletRequest request,
                                             HttpStatus status,
                                             String message,
                                             String errorId) {
        return ErrorResponse.builder()
                .message(message != null ? message : e.getMessage())
                .error(status.getReasonPhrase())
                .status(status.value())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .exception(e.getClass().getSimpleName())
                .errorId(errorId)
                .build();
    }
}
