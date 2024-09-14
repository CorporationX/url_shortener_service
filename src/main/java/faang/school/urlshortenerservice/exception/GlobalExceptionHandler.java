package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        objectError -> ((FieldError) objectError).getField(),
                        objectError -> Objects.requireNonNullElse(objectError.getDefaultMessage(), ""))
                );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exception(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorResponse(LocalDateTime.now() ,exception.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidExceptionException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorResponse(LocalDateTime.now() ,exception.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse throwableException(Throwable exception) {
        log.error("Unhandled exception: ", exception);
        return new ErrorResponse(LocalDateTime.now() ,"An unknown error has occurred, please contact support.");
    }
}
