package faang.school.urlshortenerservice.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
//        log.error("Constraint Violation Exception", ex);
//        return new ErrorResponse("Constraint Violation Exception", ex.getMessage());
//    }
//
//    @ExceptionHandler(ValidationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidationException(ValidationException ex) {
//        log.error("Entity Not Found", ex);
//        return new ErrorResponse("Entity Not Found", ex.getMessage());
//    }
//
//    @ExceptionHandler(NoSuchElementException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleNoSuchElementException(NoSuchElementException ex) {
//        log.error("No Such Element Exception", ex);
//        return new ErrorResponse("No Such Element Exception", ex.getMessage());
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleRuntimeException(RuntimeException ex) {
//        log.error("Runtime exception", ex);
//        return new ErrorResponse("Runtime exception", ex.getMessage());
//    }
//
//    @ExceptionHandler(EntityNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
//        log.error("Entity Not Found Exception", ex);
//        return new ErrorResponse("Entity Not Found Exception", ex.getMessage());
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
//        log.error("Unsupported data type", ex);
//        return new ErrorResponse("Unsupported data type:", ex.getMessage());
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(DataValidationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleDataValidationException(DataValidationException ex) {
//        log.error("Data Validation Error", ex);
//        return new ErrorResponse("Data Validation Exception", ex.getMessage());
//    }
//
//    @ExceptionHandler(EventProcessingException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleEventProcessingException(EventProcessingException ex) {
//        log.error("Event Processing Exception", ex);
//        return new ErrorResponse("Event Processing Exception", ex.getMessage());
//    }
//
//    @ExceptionHandler(EventPublishingException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleEventPublishingException(EventPublishingException ex) {
//        log.error("Event Publishing Exception", ex);
//        return new ErrorResponse("Event Publishing Exception", ex.getMessage());
//    }
}
