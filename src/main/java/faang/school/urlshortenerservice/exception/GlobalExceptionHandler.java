package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception e) {
        return ResponseEntity.status(BAD_REQUEST).body(getErrorResponse(BAD_REQUEST.value(), e));
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerExceptions(Exception e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(getErrorResponse(INTERNAL_SERVER_ERROR.value(), e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(getUncaughtErrorResponse(e));
    }

    private ErrorResponse getErrorResponse(int status, Exception e) {
        log.error("{}", e.toString());
        return createErrorResponse(status, e.getMessage());
    }

    private ErrorResponse getUncaughtErrorResponse(Exception e) {
        log.error("[UNEXPECTED ERROR]: {}", e.toString());
        return createErrorResponse(INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    private ErrorResponse createErrorResponse(int status, String message) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
