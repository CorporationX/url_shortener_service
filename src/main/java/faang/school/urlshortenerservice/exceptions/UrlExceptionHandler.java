package faang.school.urlshortenerservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> ValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> mapOfErrors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String nameOfField = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            mapOfErrors.put(nameOfField, errorMessage);
        });
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(mapOfErrors);
    }


    @ExceptionHandler({IllegalArgumentException.class, IOException.class})
    public ResponseEntity<Object> mistakeInputException(RuntimeException e) {
        return badRequest(e);
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object>serverErrors(Exception e) {
        return internalServerError(e);
    }


    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object> objectNotFoundExceptions(ObjectNotFoundException e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return ResponseEntity
                .notFound()
                .build();
    }


    public ResponseEntity<Object> internalServerError(Exception e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return ResponseEntity
                .internalServerError()
                .body(e.getMessage());
    }

    public ResponseEntity<Object> badRequest(Exception e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}

