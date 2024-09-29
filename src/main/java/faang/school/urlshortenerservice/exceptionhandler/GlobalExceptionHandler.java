package faang.school.urlshortenerservice.exceptionhandler;

import faang.school.urlshortenerservice.exception.UrlNotExistException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseException handleUrlNotFoundException(UrlNotFoundException e) {
        log.error(e.getMessage(), e);

        var errorDetails = buildErrorResponseFromException(HttpStatus.NOT_FOUND, e);
        errorDetails.setProperty("url", e.nonExistentUrl);

        return new ErrorResponseException(
                HttpStatus.NOT_FOUND,
                errorDetails,
                e
        );
    }

    @ExceptionHandler(UrlNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseException handleUrlNotExistException(UrlNotExistException e) {
        log.error(e.getMessage(), e);

        var errorDetails = buildErrorResponseFromException(HttpStatus.NOT_FOUND, e);
        errorDetails.setProperty("id", e.notExistentUlrId);

        return new ErrorResponseException(
                HttpStatus.NOT_FOUND,
                errorDetails,
                e
        );
    }

    private ProblemDetail buildErrorResponseFromException(HttpStatus status, Throwable cause) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                status,
                cause.getMessage()
        );

        addProperties(detail);

        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseException handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        Map<String, String> invalidFields = e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                                objectError -> ((FieldError) objectError).getField(),
                                objectError -> Objects.requireNonNullElse(
                                        objectError.getDefaultMessage(),
                                        "invalid")
                        )
                );

        return buildMapErrorResponse(
                HttpStatus.BAD_REQUEST,
                "validation failed",
                e,
                "invalid fields",
                invalidFields
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseException handleException(Exception e) {
        log.error(e.getMessage(), e);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal error occurred while processing your request."
        );

        addProperties(detail);

        return new ErrorResponseException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                detail,
                e
        );
    }

    private ErrorResponseException buildMapErrorResponse(
            HttpStatus status,
            String errorDescription,
            Throwable cause,
            String mapName,
            Map<String, String> map) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                status,
                errorDescription != null ? errorDescription : cause.getMessage()
        );

        addProperties(detail);

        detail.setProperty(mapName, map);

        return new ErrorResponseException(
                status,
                detail,
                cause
        );
    }

    private void addProperties(ProblemDetail detail) {
        detail.setType(URI.create("error"));
        detail.setProperty("date", LocalDateTime.now());
    }
}
