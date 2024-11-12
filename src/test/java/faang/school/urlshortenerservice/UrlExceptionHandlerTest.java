package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exeption.handler.UrlExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlExceptionHandlerTest {
    @InjectMocks
    private UrlExceptionHandler urlExceptionHandler;

    private static final String SERVICE_NAME = "TestService";

    @BeforeEach
    void setUp() {
        urlExceptionHandler = new UrlExceptionHandler();
        ReflectionTestUtils.setField(urlExceptionHandler, "serviceName", SERVICE_NAME);
    }

    private MethodArgumentNotValidException getMethodArgumentNotValidException(String field, String message) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", field, message));
        try {
            Method method = this.getClass().getDeclaredMethod("dummyMethod", String.class);
            return new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void dummyMethod(String param) {
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestStatus() {
        MethodArgumentNotValidException exception = getMethodArgumentNotValidException("field1", "Invalid value");

        ErrorResponse response = urlExceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Validation error", response.getMessage());
        assertEquals(SERVICE_NAME, response.getServiceName());
        assertEquals(Map.of("field1", "Invalid value"), response.getDetails());
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundStatus() {
        String message = "Entity not found";
        ErrorResponse expectedResponse = ErrorResponse.builder()
                .serviceName(SERVICE_NAME)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(message)
                .build();

        EntityNotFoundException exception = new EntityNotFoundException(message);

        ErrorResponse actualResponse = urlExceptionHandler.handleEntityNotFoundException(exception);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerErrorStatus() {
        String message = "Internal server error. Please try again later.";
        ErrorResponse expectedResponse = ErrorResponse.builder()
                .serviceName(SERVICE_NAME)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .build();

        RuntimeException exception = new RuntimeException("Unexpected error");

        ErrorResponse actualResponse = urlExceptionHandler.handleRuntimeException(exception);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void handleAllOtherExceptions_ShouldReturnInternalServerErrorStatus() {
        String message = "An error occurred. Please contact support.";
        ErrorResponse expectedResponse = ErrorResponse.builder()
                .serviceName(SERVICE_NAME)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .build();

        Exception exception = new Exception("Unexpected error");

        ErrorResponse actualResponse = urlExceptionHandler.handleAllOtherExceptions(exception);

        assertEquals(expectedResponse, actualResponse);
    }
}