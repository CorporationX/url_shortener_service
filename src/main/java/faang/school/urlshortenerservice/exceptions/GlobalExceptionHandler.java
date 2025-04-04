package faang.school.urlshortenerservice.exceptions;

import faang.school.urlshortenerservice.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UrlNotFoundException.class})
    public ResponseEntity<ErrorDto> handleNotFoundException(RuntimeException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Bad request: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Internal server error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorDto> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        ErrorDto errorResponse = new ErrorDto(
                status.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
