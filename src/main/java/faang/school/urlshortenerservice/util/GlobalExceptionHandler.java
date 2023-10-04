package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.dto.Error;
import faang.school.urlshortenerservice.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException e) {
        var bindingResult = e.getBindingResult();
        Map<String, String> fieldErrors = new HashMap<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            });
        }
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(fieldErrors);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Error> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(fromBusinessException(e));
    }

    private Error fromBusinessException(BusinessException e) {
        return new Error(
                e.getCode(),
                e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error(String.valueOf(ex.getCause()));
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
