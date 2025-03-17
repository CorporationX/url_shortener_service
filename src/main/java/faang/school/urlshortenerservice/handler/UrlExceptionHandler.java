package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException e,
                                                                    HttpServletRequest request) {
        log.warn("Ошибка: {}", e.getMessage(), e);
        return response(e.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidException(MethodArgumentNotValidException e,
                                                              HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Ошибка валидации: {}", errorMessage);
        return response(errorMessage, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e,
                                                                HttpServletRequest request) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        return response("Внутренняя ошибка сервера", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> response(String message,
                                                   HttpServletRequest request,
                                                   HttpStatus status) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .url(request.getRequestURI())
                .message(message)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
