package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.error.UrlShortenerErrorResponseDto;
import faang.school.urlshortenerservice.exception.authorization.UserUnauthorizedException;
import faang.school.urlshortenerservice.exception.url.HashNotFoundException;
import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UrlShortenerExceptionHandler {
    private static final Map<Class<? extends Exception>, HttpStatus> HTTP_STATUS_MAP = new HashMap<>();

    static {
        HTTP_STATUS_MAP.put(UserUnauthorizedException.class, HttpStatus.UNAUTHORIZED);
        HTTP_STATUS_MAP.put(HashNotFoundException.class, HttpStatus.NOT_FOUND);
        HTTP_STATUS_MAP.put(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
        HTTP_STATUS_MAP.put(FeignException.class, HttpStatus.BAD_GATEWAY);
        HTTP_STATUS_MAP.put(RetryableException.class, HttpStatus.BAD_GATEWAY);
    }

    private static final Map<Class<? extends Exception>, ErrorHandler> errorHandlers = Map.of(
            MethodArgumentNotValidException.class, ex ->
                    formatMethodArgumentNotValidException((MethodArgumentNotValidException) ex)
    );

    @ExceptionHandler({
            UserUnauthorizedException.class,
            HashNotFoundException.class,
            MethodArgumentNotValidException.class,
            FeignException.class,
            RetryableException.class
    })
    public ResponseEntity<UrlShortenerErrorResponseDto> handleException(Exception ex) {
        ErrorHandler handler = getErrorHandler(ex);
        String errorMessage = handler.handle(ex);
        HttpStatus status = getHttpStatus(ex);

        return createErrorResponse(errorMessage, status, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UrlShortenerErrorResponseDto> handleServerException(Exception ex) {
        log.error("Unhandled exception caught", ex);
        return createErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private HttpStatus getHttpStatus(Throwable ex) {
        return HTTP_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorHandler getErrorHandler(Throwable ex) {
        return errorHandlers.getOrDefault(ex.getClass(), Throwable::getMessage);
    }

    private ResponseEntity<UrlShortenerErrorResponseDto> createErrorResponse(String errorMsg,
                                                                               HttpStatus status,
                                                                               Exception ex) {
        log.error("Error in url-shortener-service: {}, response status {}", errorMsg, status, ex);
        UrlShortenerErrorResponseDto response =
                new UrlShortenerErrorResponseDto(errorMsg, LocalDateTime.now(), status.value());
        return new ResponseEntity<>(response, status);
    }

    private static String formatMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .map(error -> String.format("Field '%s' %s",
                        ((FieldError) error).getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
    }
}
