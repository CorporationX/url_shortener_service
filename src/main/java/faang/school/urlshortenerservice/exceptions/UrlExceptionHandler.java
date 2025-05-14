package faang.school.urlshortenerservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler({
            InvalidHashException.class,
            InvalidUrlException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        return buildResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            HashGenerationException.class,
            CacheOperationException.class
    })
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        return buildResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildResponseEntity("Произошла ошибка на сервере", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(String message, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(message), status);
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
