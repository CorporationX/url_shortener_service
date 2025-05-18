package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        log.warn("Validation failed: ", ex);
        return buildResponse(ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(NotFoundException ex) {
        log.warn("URL not found: {}", ex.getMessage());
        return buildResponse(ex);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(InternalException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildResponse(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal server error occurred"));
    }

    private ResponseEntity<ErrorResponse> buildResponse(ApiException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(ex.getHttpStatus().value())
                        .message(ex.getUserMessage())
                        .build());
    }
}
