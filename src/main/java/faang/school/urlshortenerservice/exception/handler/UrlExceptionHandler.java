package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.dto.ExceptionResponse;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    // Обработка ошибок валидации (например @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation error: {}", message);

        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    // Обработка внутренних ошибок приложения
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        log.error("Internal server error: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    // Дефолтный обработчик для всех остальных исключений
    @ExceptionHandler(Exception.class) //TODO может и не нуджен
    public ResponseEntity<ExceptionResponse> handleAllExceptions(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unforeseen mistake occurred. Try it later.",
                request.getRequestURI());
    }

    // Приватный метод для сборки ответа
    private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, String message, String path) {
        ExceptionResponse response = ExceptionResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.warn("URL not found: {}", ex.getMessage());
        ExceptionResponse response = ExceptionResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
