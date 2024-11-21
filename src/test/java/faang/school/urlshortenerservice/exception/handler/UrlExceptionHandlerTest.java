package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.dto.ErrorResponse;
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


    private MethodArgumentNotValidException getMethodArgumentNotValidException(String message) throws NoSuchMethodException {
        Method method = this.getClass().getDeclaredMethod("handleMethodArgumentNotValidException_ShouldReturnBadRequestStatusWithErrors");
        MethodParameter methodParameter = new MethodParameter(method, -1);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "field", message));
        return new MethodArgumentNotValidException(methodParameter, bindingResult);
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestStatusWithErrors() throws NoSuchMethodException {
        String message = "Validation failed for argument";
        ErrorResponse correctResult = ErrorResponse.builder()
                .serviceName(SERVICE_NAME)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .details(Map.of("field", message))
                .build();
        MethodArgumentNotValidException exception = getMethodArgumentNotValidException(message);

        ErrorResponse result = urlExceptionHandler.handleValidationException(exception);

        assertEquals(correctResult, result);
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
    void handleException_ShouldReturnInternalServerErrorStatus() {
        String message = "Unexpected error";
        ErrorResponse expectedResponse = ErrorResponse.builder()
                .serviceName(SERVICE_NAME)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .build();

        Exception exception = new Exception(message);

        ErrorResponse actualResponse = urlExceptionHandler.handleRuntimeException(exception);

        assertEquals(expectedResponse, actualResponse);
    }
}