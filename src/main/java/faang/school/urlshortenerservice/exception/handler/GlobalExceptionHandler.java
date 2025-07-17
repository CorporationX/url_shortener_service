package faang.school.urlshortenerservice.exception.handler;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.rmi.ServerError;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({IllegalArgumentException.class, IOException.class})
    public ResponseEntity<Object> handleWrongInputException(RuntimeException ex) {
        return badRequest(ex);
    }

    @ExceptionHandler({
            RuntimeException.class,
            ServletException.class,
            ServerError.class,
            ExecutionException.class,
            RejectedExecutionException.class,
            ExecutionException.class,
            TaskRejectedException.class
    })
    public ResponseEntity<Object> handleServerErrors(Exception ex) {
        return internalServerError(ex);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object> handleObjectNotFoundExceptions(ObjectNotFoundException ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        return ResponseEntity
                .notFound()
                .build();
    }

    private ResponseEntity<Object> internalServerError(Exception ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        return ResponseEntity
                .internalServerError()
                .body(ex.getMessage());
    }

    private ResponseEntity<Object> badRequest(Exception ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
