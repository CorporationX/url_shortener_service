package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UrlExceptionHandler {
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException ex) {
        return new ResponseEntity<>("Произошла непредвиденная ошибка: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>("Произошла ошибка валидации: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleDefaultException(Exception ex) {
        return new ResponseEntity<>("Произошла ошибка на сервере. Пожалуйста, попробуйте позже.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
