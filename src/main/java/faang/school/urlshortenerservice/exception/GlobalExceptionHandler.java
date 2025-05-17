package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String UNEXPECTED_EXCEPTION_MESSAGE = "INTERNAL UNEXPECTED SERVER ERROR";

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptionsWithStatusBadRequest(Exception e) {
        return ResponseEntity.status(BAD_REQUEST).body(getErrorResponse(e));
    }

    @ExceptionHandler({
            UrlNotFoundException.class,
            HashNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptionsWithStatusNotFound(Exception e) {
        return ResponseEntity.status(NOT_FOUND).body(getErrorResponse(e));
    }

    @ExceptionHandler({
            DataAccessException.class
    })
    public ResponseEntity<ErrorResponse> handleInternalServerExceptions(Exception e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(getErrorResponse(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(getUncaughtErrorResponse(e));
    }

    private ErrorResponse getErrorResponse(Exception e) {
        log.error("{}/n{}", e.getMessage(), e.getStackTrace());
        return createErrorResponse(e.getMessage());
    }

    private ErrorResponse getUncaughtErrorResponse(Exception e) {
        log.error("[UNEXPECTED ERROR]: {}/n{}", e.getMessage(), e.getStackTrace());
        return createErrorResponse(UNEXPECTED_EXCEPTION_MESSAGE);
    }

    private ErrorResponse createErrorResponse(String message) {
        return ErrorResponse.builder()
                .message(message)
                .build();
    }
}
