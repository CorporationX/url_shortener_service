package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("Object {} is in an unsuitable state for the operation being performed {}", uri, ex.getMessage());
        return new ErrorResponse(ex.getMessage(), uri);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(Exception ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("Method called with invalid argument {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage(), uri);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("Url not found at {}. Details: {}", uri, e.getMessage());
        return new ErrorResponse(e.getMessage(), uri);
    }


    @ExceptionHandler(value = {RedisConnectionFailureException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleRedisConnectionFailureExceptions(RedisConnectionFailureException e) {
        log.error("Failed to connect to Redis: {}", e.getMessage());
        return new ErrorResponse("Failed to connect to Redis", e.getMessage());
    }

    @ExceptionHandler(value = {JedisConnectionException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleJedisConnectionExceptions(JedisConnectionException e) {
        log.error("Failed to connect to Redis: {}", e.getMessage());
        return new ErrorResponse("Failed to connect to Redis", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse globalExceptionHandle(Exception e, WebRequest request) {
        log.error("An unexpected exception occurred. Details: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), request.getDescription(false));
    }
}