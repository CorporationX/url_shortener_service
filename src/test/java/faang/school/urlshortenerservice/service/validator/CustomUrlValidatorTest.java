package faang.school.urlshortenerservice.service.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CustomUrlValidatorTest {

    private CustomUrlValidator validator = new CustomUrlValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void testValidUrl() {
        String validUrl = "http://www.example.com";
        assertTrue(validator.isValid(validUrl, context));
    }

    @Test
    void testUrlWithoutHost() {
        String invalidUrl = "http://";
        assertFalse(validator.isValid(invalidUrl, context));
    }

    @Test
    void testInvalidUrl() {
        String invalidUrl = "invalid-url";
        assertFalse(validator.isValid(invalidUrl, context));
    }

    @Test
    void testValidUrlWithHost() {
        String validUrl = "http://www.google.com";
        assertTrue(validator.isValid(validUrl, context));
    }
}