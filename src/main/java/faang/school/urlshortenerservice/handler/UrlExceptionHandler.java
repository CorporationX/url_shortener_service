package faang.school.urlshortenerservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(InternalError.class)
    public ResponseEntity<String> internalSystemErrors(InternalError e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка системы: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> defaultHandlerOfAllExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка сервера: " + e.getMessage());
    }

//    public ResponseEntity<String> errorOfValidation() {
//        return
//    }
}
