package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UrlNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e, HttpServletRequest request) {
        log.error("Error: {}", e.getMessage());
        return getErrorResponse(request.getRequestURI(), HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        String errorMessage = fieldErrors.stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Ошибка валидации: {}", errorMessage);
        return getErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e, HttpServletRequest request) {
        log.error("Error: {}", e.getMessage());
        return getErrorResponse(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ErrorResponse getErrorResponse(String url, HttpStatus status, String message) {
        return ErrorResponse.builder()
                .url(url)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

}
