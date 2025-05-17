package faang.school.urlshortenerservice.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInInvalidUrlException(InvalidUrlException e) {
        log.error("Url адрес не действителен: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUrlNotFoundException(UrlNotFoundException e) {
        log.error("Url адрес не найден {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.error("Ошибка валидации данных");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Exception e) {
        log.error("Произошла ошибка {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    public record ErrorResponse(String message) {}
}
