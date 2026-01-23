package faang.school.urlshortenerservice.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.ofEntries(
            Map.entry(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST),
            Map.entry(FeignException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(RuntimeException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(IllegalArgumentException.class, HttpStatus.BAD_REQUEST),
            Map.entry(ConstraintViolationException.class, HttpStatus.BAD_REQUEST),
            Map.entry(UrlNotFoundException.class, HttpStatus.NOT_FOUND)
    );

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e, HttpServletRequest rq) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(e.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = buildMessage(e, status);
        log.error("[{} {}] -> {}", rq.getMethod(), rq.getRequestURL(), e.getMessage(), e);

        return new ErrorResponse(
                LocalDateTime.now(),
                rq.getRequestURL().toString(),
                e.getClass().getSimpleName(),
                message,
                status.value()
        );
    }

    private String buildMessage(Exception e, HttpStatus status) {
        if (status.is4xxClientError()) {
            if (e instanceof MethodArgumentNotValidException ex) {
                return ex.getBindingResult().getAllErrors().stream()
                        .map(error -> {
                            if (error instanceof FieldError fieldError) {
                                return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                            }
                            return error.getDefaultMessage();
                        })
                        .collect(Collectors.joining("; "));
            }
            return e.getMessage();
        }
        return "Internal server error";
    }
}


