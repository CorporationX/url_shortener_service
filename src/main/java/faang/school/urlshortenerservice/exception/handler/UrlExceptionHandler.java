package faang.school.urlshortenerservice.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.urlshortenerservice.exception.UrlNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.zip.DataFormatException;

@ControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(DataFormatException.class)
    public ResponseEntity<Object> handleDataFormatException(DataFormatException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UrlNotExistException.class)
    public ResponseEntity<Object> handleUrlNotExistException(UrlNotExistException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({JsonProcessingException.class})
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
