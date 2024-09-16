package faang.school.urlshortenerservice.exception.handler;

import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
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

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(Exception ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("No element found " + uri, ex.getMessage());
        return new ErrorResponse(ex.getMessage(), uri);

    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateException(Exception ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("Object {} is in an unsuitable state for the operation being performed " + uri, ex.getMessage());
        return new ErrorResponse(ex.getMessage(), uri);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(Exception ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("Method is called with invalid argument " + ex.getMessage());
        return new ErrorResponse(ex.getMessage(), uri);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMessageNotReadable(Exception ex, WebRequest request) {
        log.error("Request body coming into controller method, unreadable" + ex.getMessage());
        return new ErrorResponse(ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handlerRedisConnectionFailureException(RedisConnectionFailureException ex){
        log.error("Failed to connect to Redis", ex);
        return "Failed to connect to Redis. " + ex.getMessage();
    }

    @ExceptionHandler(RedisConnectionException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleRedisConnectionException(RedisConnectionException ex){
        log.error("Redis connection error: ", ex);
        return "Redis service is currently unavailable. Please try again later. " + ex.getMessage();
    }

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("URL not found" + ex.getMessage());
        return new ErrorResponse(ex.getMessage(), uri);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneralException(RuntimeException ex, WebRequest request) {
        return new ErrorResponse(ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGlobalException(Exception ex, WebRequest request) {
        return new ErrorResponse(ex.getMessage(), request.getDescription(false));
    }
}