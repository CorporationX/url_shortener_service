package faang.school.urlshortenerservice.handler;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.MalformedInputException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleInternalErrors(RuntimeException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getMessage(), 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<ErrorResponseDto> handleURISyntaxException(URISyntaxException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getMessage(), 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<ErrorResponseDto> handleMalformatedUrlException(MalformedInputException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getMessage(), 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getMessage(), 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getMessage(), 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }
}
