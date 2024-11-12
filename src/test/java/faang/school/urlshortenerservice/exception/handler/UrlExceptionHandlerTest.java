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

        ResponseEntity<ErrorResponse> response = urlExceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertEquals("An error occurred, please contact support", Objects.requireNonNull(errorResponse).getMessage());
        assertEquals(SERVICE_NAME, errorResponse.getServiceName());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnBadRequestResponse() {
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError error1 = new FieldError("object", "field1", "Field1 is required");
        FieldError error2 = new FieldError("object", "field2", "Field2 cannot be empty");
        fieldErrors.add(error1);
        fieldErrors.add(error2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodParameter methodParameter = mock(MethodParameter.class);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = urlExceptionHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertEquals(SERVICE_NAME, errorResponse.getServiceName());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());

        Map<String, String> details = errorResponse.getDetails();
        assertEquals(2, details.size());
        assertEquals("Field1 is required", details.get("field1"));
        assertEquals("Field2 cannot be empty", details.get("field2"));
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnDefaultMessageWhenNoMessageProvided() {
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError error = new FieldError("object", "field1", null);
        fieldErrors.add(error);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = urlExceptionHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertEquals(SERVICE_NAME, errorResponse.getServiceName());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());

        Map<String, String> details = errorResponse.getDetails();
        assertEquals(1, details.size());
        assertEquals("Invalid input for field field1", details.get("field1"));
    }

    @Test
    void handleException_shouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception("Test general exception");

        ResponseEntity<ErrorResponse> response = urlExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertEquals("Error occurred", errorResponse.getMessage());
        assertEquals(SERVICE_NAME, errorResponse.getServiceName());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
    }
}