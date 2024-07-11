package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.ErrorResponse;
import faang.school.urlshortenerservice.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(e, request);
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .message(e.getMessage())
                .build();
    }
}
