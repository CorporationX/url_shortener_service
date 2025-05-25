package faang.school.urlshortenerservice.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class UrlShortServiceExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        HttpStatus httpStatus = ExceptionMapping.getHttpStatus(ex.getClass());
        String message = ex.getMessage();

        log.error("Exception caught by {}: {}", ex.getClass().getSimpleName(), message);

        return ResponseEntity.status(httpStatus).body(Map.of("error", message));
    }
}
