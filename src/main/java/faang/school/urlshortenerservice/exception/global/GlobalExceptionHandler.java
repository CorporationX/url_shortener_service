package faang.school.urlshortenerservice.exception.global;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import faang.school.urlshortenerservice.exception.common.RecordNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Map<Class<? extends Exception>, HttpStatus> HTTP_STATUS_MAP = new HashMap<>();

    static {
        HTTP_STATUS_MAP.put(RecordNotFoundException.class, HttpStatus.NOT_FOUND);
        HTTP_STATUS_MAP.put(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
        HTTP_STATUS_MAP.put(FeignException.class, HttpStatus.BAD_GATEWAY);
        HTTP_STATUS_MAP.put(RetryableException.class, HttpStatus.BAD_GATEWAY);
    }

    private static final Map<Class<? extends Exception>, ErrorHandler> errorHandlers = Map.of(
            MethodArgumentNotValidException.class, String::valueOf
    );

    @ExceptionHandler({
            RecordNotFoundException.class,
            MethodArgumentNotValidException.class,
            FeignException.class,
            RetryableException.class
    })
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorHandler handler = getErrorHandler(ex);
        String errorMessage = handler.handle(ex);
        HttpStatus status = getHttpStatus(ex);

        return createErrorResponse(errorMessage, status, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerException(Exception ex) {
        log.error("Unhandled exception caught", ex);
        return createErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private HttpStatus getHttpStatus(Throwable ex) {
        return HTTP_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorHandler getErrorHandler(Throwable ex) {
        return errorHandlers.getOrDefault(ex.getClass(), Throwable::getMessage);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(String errorMsg, HttpStatus status,
                                                              Exception ex) {
        log.error("Error in url-shortener-service: {}, response status {}", errorMsg, status, ex);
        ErrorResponse response = new ErrorResponse(errorMsg, LocalDateTime.now(), status.value());
        return new ResponseEntity<>(response, status);
    }
}