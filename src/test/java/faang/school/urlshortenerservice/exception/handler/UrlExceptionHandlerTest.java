package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.DataValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlExceptionHandlerTest {

    private UrlExceptionHandler exceptionHandler;
    private String serviceName;

    @BeforeEach
    void setUp() {
        exceptionHandler = new UrlExceptionHandler();
        serviceName = "TestService";
        ReflectionTestUtils.setField(exceptionHandler, "serviceName", serviceName);
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundStatus() {
        String message = "Entity not found";
        ErrorResponse correctResult = ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.NOT_FOUND.value())
                .globalMessage(message)
                .build();
        PersistenceException exception = new EntityNotFoundException(message);

        ErrorResponse result = exceptionHandler.handleEntityNotFoundException(exception);

        assertEquals(correctResult, result);
    }

    @Test
    void handleEntityExistsException_ShouldReturnNotFoundStatus() {
        String message = "Entity exists";
        ErrorResponse correctResult = ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .globalMessage(message)
                .build();
        PersistenceException exception = new EntityExistsException(message);

        ErrorResponse result = exceptionHandler.handleEntityExistsException(exception);

        assertEquals(correctResult, result);
    }

    @Test
    void handleDataValidationException_ShouldReturnBadRequestStatus() {
        String message = "Validation error";
        ErrorResponse correctResult = ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .globalMessage(message)
                .build();
        DataValidationException exception = new DataValidationException(message);

        ErrorResponse result = exceptionHandler.handleDataValidationException(exception);

        assertEquals(correctResult, result);
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestStatusWithErrors() {
        String message = "must not be null";
        ErrorResponse correctResult = ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorDetails(Map.of("field", message))
                .build();
        MethodArgumentNotValidException exception = getMethodArgumentNotValidException(message);

        ErrorResponse result = exceptionHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(correctResult, result);
    }

    @Test
    void handleDataIntegrityViolation_shouldReturnErrorResponse() {
        String message = "Data Integrity Violation Error";
        ErrorResponse correctResult = ErrorResponse.builder()
                .serviceName(serviceName)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .globalMessage(message)
                .build();
        DataIntegrityViolationException exception = new DataIntegrityViolationException(message);

        ErrorResponse result = exceptionHandler.handleDataIntegrityViolation(exception);

        assertEquals(correctResult, result);
    }

    private MethodArgumentNotValidException getMethodArgumentNotValidException(String message) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "field", message));
        return new MethodArgumentNotValidException((MethodParameter) null, bindingResult);
    }
}
