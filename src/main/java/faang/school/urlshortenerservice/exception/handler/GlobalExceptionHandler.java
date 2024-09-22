package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.Base62EncoderException;
import faang.school.urlshortenerservice.exception.ErrorResponse;
import faang.school.urlshortenerservice.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error occurred", e.getMessage(), LocalDateTime.now(), request.getDescription(false));
        log.error("Unknown exception was occurred", e);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not found", e.getMessage(), LocalDateTime.now(), request.getDescription(false));
        log.error("not found exception was occurred", e);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Base62EncoderException.class)
    public ResponseEntity<ErrorResponse> handleBase62EncoderException(Base62EncoderException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), "Base62 encoding error", e.getMessage(), LocalDateTime.now(), request.getDescription(false));
        log.error("Base62 encoding error was occurred", e);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
