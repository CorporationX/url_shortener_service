package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationError(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        Map<String, List<String>> response = new LinkedHashMap<>();
        response.put("type", List.of("Validation error"));
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Map<String, List<String>>> handleUrlNotFoundError(UrlNotFoundException ex) {

        Map<String, List<String>> response = new LinkedHashMap<>();
        response.put("type", List.of("Not found"));
        response.put("hash", List.of(ex.getHash()));
        response.put("message", List.of(ex.getMessage()));

        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<String>>> handleError(Exception ex) {

        Map<String, List<String>> response = new LinkedHashMap<>();
        response.put("type", List.of("Internal error"));
        response.put("message", List.of(ex.getMessage()));

        return ResponseEntity.internalServerError().body(response);
    }

}
