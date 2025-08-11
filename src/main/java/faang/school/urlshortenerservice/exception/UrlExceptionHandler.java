package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.exception.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class UrlExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error occurred: {}", ex.getMessage(), ex);
        return handleBindingErrors(ex.getBindingResult(), "Validation error");
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindExceptions(BindException ex) {
        log.error("Binding error occurred: {}", ex.getMessage(), ex);
        return handleBindingErrors(ex.getBindingResult(), "Binding error");
    }

    private ResponseEntity<ErrorResponse> handleBindingErrors(BindingResult bindingResult, String defaultMessage) {
        String errorMessage = bindingResult.getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse(defaultMessage);

        return createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InternalError.class)
    public ResponseEntity<ErrorResponse> handleInternalErrors(InternalError ex) {
        log.error("Internal error occurred: {}", ex.getMessage(), ex);
        return createErrorResponse("Internal server error occurred: " + ex.getMessage(),
                                  HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(String message, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .status(status.value())
                .build();

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}

