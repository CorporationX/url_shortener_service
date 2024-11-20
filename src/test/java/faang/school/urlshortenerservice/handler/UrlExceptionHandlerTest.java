package faang.school.urlshortenerservice.handler;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlExceptionHandlerTest {

    private UrlExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new UrlExceptionHandler();
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundStatus() {
        String message = "Entity not found";
        ErrorResponse expectedResponse = ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(message)
                .build();

        EntityNotFoundException exception = new EntityNotFoundException(message);
        ErrorResponse result = exceptionHandler.handleEntityNotFoundException(exception);

        assertEquals(expectedResponse, result);
    }

    @Test
    void handleDataValidationException_ShouldReturnBadRequestStatus() {
        String message = "Validation error";
        ErrorResponse expectedResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();

        DataValidationException exception = new DataValidationException(message);
        ErrorResponse result = exceptionHandler.handleDataValidationException(exception);

        assertEquals(expectedResponse, result);
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestStatusWithErrors() {
        String fieldName = "field";
        String errorMessage = "must not be null";
        Map<String, String> validationErrors = Map.of(fieldName, errorMessage);

        ErrorResponse expectedResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed for fields: " + validationErrors)
                .build();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", fieldName, errorMessage));

        MethodParameter methodParameter = new MethodParameter(this.getClass().getDeclaredMethods()[0], -1);

        MethodArgumentNotValidException exception =  new MethodArgumentNotValidException(methodParameter, bindingResult);

        ErrorResponse result = exceptionHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(expectedResponse, result);
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerErrorStatus() {
        String message = "Unexpected server error";
        ErrorResponse expectedResponse = ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .build();

        RuntimeException exception = new RuntimeException(message);
        ErrorResponse result = exceptionHandler.handleRuntimeException(exception);

        assertEquals(expectedResponse, result);
    }
}
