package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.ErrorResponse;
import faang.school.urlshortenerservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error occurred", e.getMessage(), LocalDateTime.now(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not found", e.getMessage(), LocalDateTime.now(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
//        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad request", e.getMessage(), LocalDateTime.now(), request.getDescription(false));
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
}
