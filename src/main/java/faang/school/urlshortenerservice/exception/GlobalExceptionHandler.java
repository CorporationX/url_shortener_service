package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            ResourceNotFoundException.class,
            BadRequestException.class
    })
    public ResponseEntity<Object> handleCustomExceptions(RuntimeException ex) {
        HttpStatus status = resolveHttpStatus(ex);
        String message = ex.getMessage();
        log.error("Exception occurred: {}, message: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        String message = ex.getMessage();
        log.error("An unexpected error occurred. {}", ex.getMessage(), ex);
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus resolveHttpStatus(Exception ex) {
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value();
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
