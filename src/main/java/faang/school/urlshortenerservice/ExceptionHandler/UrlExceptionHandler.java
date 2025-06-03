package faang.school.urlshortenerservice.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        return buildResponse(Errors.VALIDATION_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage());
        return buildResponse(Errors.INVALID_ARGUMENT);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.error(ex.getMessage());
        return buildResponse(ex.getError());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildResponse(Errors.INTERNAL_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponse(Errors error) {
        return ResponseEntity.status(error.getStatus()).body(ErrorResponse.builder()
                .status(error.getStatus().value())
                .error(error)
                .message(error.getMessage())
                .build());
    }
}
