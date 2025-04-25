package faang.school.urlshortenerservice.excecption.handler;

import faang.school.urlshortenerservice.excecption.InvalidUrlException;
import faang.school.urlshortenerservice.excecption.OriginalUrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException ex) {
        String message = "Please enter a valid URL";
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, message)
                .title("Invalid URL")
                .detail(message)
                .property("Service", "URL shortener service")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OriginalUrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(OriginalUrlNotFoundException ex) {
        String message = "Please enter a URl that exists";
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, message)
                .title("Unknown URL")
                .detail(message)
                .property("Service", "URL shortener service")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
