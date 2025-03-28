package faang.school.urlshortenerservice.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import faang.school.urlshortenerservice.dto.ErrorDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorDto handleEntityNotFoundException(final EntityNotFoundException exception,
                                                  final HttpServletRequest request) {
        String errorMessage = "Entity not found exception: " + exception.getMessage();
        log.error(errorMessage, exception);
        return buildErrorDto(errorMessage, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorDto handleConstraintViolationException(final ConstraintViolationException exception,
                                                       final HttpServletRequest request) {

        String baseErrorMessage = exception.getMessage();

        String validationErrorMessage = exception.getConstraintViolations().stream()
                .map(v -> String.format("%s: %s",
                        extractFieldName(v.getPropertyPath()),
                        v.getMessage()))
                .collect(Collectors.joining("; "));

        String fullMessage = String.format("%s. Validation errors: %s",
                (baseErrorMessage != null ? baseErrorMessage : "Validation failed"),
                validationErrorMessage);

        log.error(fullMessage);

        return buildErrorDto(fullMessage, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidFormatException.class, MethodArgumentTypeMismatchException.class})
    public ErrorDto handleBadRequestExceptions(final Exception exception,
                                               final HttpServletRequest request) {
        log.error(exception.getMessage());

        return buildErrorDto(exception.getMessage(), request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleCommonException(final Exception exception,
                                          final HttpServletRequest request) {
        log.error(exception.getMessage());

        return buildErrorDto(exception.getMessage(), request);
    }

    private ErrorDto buildErrorDto(final String errorMessage, final HttpServletRequest request) {

        return ErrorDto.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .message(errorMessage)
                .build();
    }

    private String extractFieldName(final Path propertyPath) {
        if (propertyPath == null) {
            return "unknown";
        }
        String pathStr = propertyPath.toString();
        // Извлекаем последнюю часть пути (после последней точки)
        return pathStr.contains(".")
                ? pathStr.substring(pathStr.lastIndexOf('.') + 1)
                : pathStr;
    }
}
