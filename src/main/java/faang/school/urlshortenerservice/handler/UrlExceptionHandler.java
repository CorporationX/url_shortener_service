package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.ErrorFieldDto;
import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<ErrorFieldDto> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            Object value = ((FieldError) error).getRejectedValue();
            errors.add(new ErrorFieldDto(((FieldError) error).getField(), error.getDefaultMessage(), value != null ? value.toString() : null));
        });
        return this.errorResponse(HttpStatus.BAD_REQUEST, "Некорректный запрос", errors);
    }

    @ExceptionHandler(HashNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleHashNotFoundException(HashNotFoundException ex) {
        return this.errorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), List.of(new ErrorFieldDto("hash", "Хэш не найден", ex.getHash())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        return this.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    private ResponseEntity<ErrorResponseDto> errorResponse(HttpStatus status, String message, List<ErrorFieldDto> errors) {
        return new ResponseEntity<>(new ErrorResponseDto(status.name(), message, errors), status);
    }
}
