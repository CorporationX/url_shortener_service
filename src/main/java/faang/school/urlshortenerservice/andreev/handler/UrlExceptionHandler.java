package faang.school.urlshortenerservice.andreev.handler;

import faang.school.urlshortenerservice.andreev.exception.HashGenerationException;
import faang.school.urlshortenerservice.andreev.exception.InvalidUrlException;
import faang.school.urlshortenerservice.andreev.exception.UrlNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UrlExceptionHandler {
    private static final String SERVICE_NAME = "url_shortener_service";

    @ExceptionHandler(UrlNotFound.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(UrlNotFound e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, "Url not found", e.getMessage());
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, "Invalid URL", e.getMessage());
    }

    @ExceptionHandler(HashGenerationException.class)
    public ResponseEntity<ErrorResponse> handleHashGenerationException(HashGenerationException e) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, "Hash Generation Failed",
                "An error occurred during hash batch generation");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, HttpStatus status, String title, String detail) {
        ErrorResponse error = ErrorResponse.builder(e, status, e.getMessage())
                .title(title)
                .detail(detail)
                .property("service", SERVICE_NAME)
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
