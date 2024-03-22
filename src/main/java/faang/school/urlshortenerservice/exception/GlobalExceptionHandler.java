package faang.school.urlshortenerservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UrlNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e, HttpServletRequest request) {
        log.error("Error: {}", e.getMessage());
        return getErrorResponse(request.getRequestURI(), HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(DataIntegrityViolationException e) {
        String message = "Ошибка целостности данных: ";
        if (e.getCause() instanceof ConstraintViolationException cve) {
            message += "нарушение ограничения - " + cve.getConstraintName();
        } else {
            message += e.getMessage();
        }
        return message;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e, HttpServletRequest request) {
        log.error("Error: {}", e.getMessage());
        return getErrorResponse(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ErrorResponse getErrorResponse(String url, HttpStatus status, String message) {
        return ErrorResponse.builder()
                .url(url)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

}
