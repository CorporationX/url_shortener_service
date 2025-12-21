package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UrlExceptionHandler {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)  // 1 пропуск
    @ExceptionHandler(NoFreeHashesException.class)   // 2 пропуск
    public ErrorResponse handleNoFreeHashes(
            NoFreeHashesException ex,                // 3 пропуск
            HttpServletRequest request) {
        return new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(), // 4 пропуск
                "Service Unavailable",                   // 5 пропуск
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllExceptions(
            Exception ex,
            HttpServletRequest request) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
        ex.getMessage(),
                request.getRequestURI()
        );
    }
}