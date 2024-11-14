package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.dto.handler.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlExceptionHandlerTest {

    @InjectMocks
    private UrlExceptionHandler urlExceptionHandler;

    @Mock
    private BindingResult bindingResult;

    private static final String SERVICE_NAME = "TestService";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlExceptionHandler, "serviceName", SERVICE_NAME);
    }

    @Test
    void handleRuntimeException_shouldReturnInternalServerErrorResponse() {
        RuntimeException exception = new RuntimeException("Test runtime exception");

        ErrorResponse errorResponse = urlExceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("An error occurred, please contact support", errorResponse.getMessage());
        assertEquals(SERVICE_NAME, errorResponse.getServiceName());
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnBadRequestResponse() throws NoSuchMethodException {
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError error1 = new FieldError("object", "field1", "Field1 is required");
        FieldError error2 = new FieldError("object", "field2", "Field2 cannot be empty");
        fieldErrors.add(error1);
        fieldErrors.add(error2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodParameter methodParameter = new MethodParameter(
                getClass().getMethod("dummyMethod", String.class), 0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ErrorResponse errorResponse = urlExceptionHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(SERVICE_NAME, errorResponse.getServiceName());
        assertEquals(2, errorResponse.getDetails().size());
        assertEquals("Field1 is required", errorResponse.getDetails().get("field1"));
        assertEquals("Field2 cannot be empty", errorResponse.getDetails().get("field2"));
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnDefaultMessageWhenNoMessageProvided() throws NoSuchMethodException {
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError error = new FieldError("object", "field1", null);
        fieldErrors.add(error);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodParameter methodParameter = new MethodParameter(
                getClass().getMethod("dummyMethod", String.class), 0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ErrorResponse errorResponse = urlExceptionHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(SERVICE_NAME, errorResponse.getServiceName());
        assertEquals(1, errorResponse.getDetails().size());
        assertEquals("Invalid input for field field1", errorResponse.getDetails().get("field1"));
    }

    @Test
    void handleException_shouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception("Test general exception");

        ErrorResponse errorResponse = urlExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Error occurred", errorResponse.getMessage());
        assertEquals(SERVICE_NAME, errorResponse.getServiceName());
    }

    public void dummyMethod(String arg) {
    }
}