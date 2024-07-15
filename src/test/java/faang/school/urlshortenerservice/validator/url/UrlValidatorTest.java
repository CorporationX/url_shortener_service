package faang.school.urlshortenerservice.validator.url;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {

    private UrlValidator urlValidator;

    @BeforeEach
    public void setUp() {
        urlValidator = new UrlValidator();
    }

    @Test
    public void testWhenUrlIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlValidator.validateUrl(null);
        });
        assertEquals("The URL can't be empty", exception.getMessage());
    }

    @Test
    public void testWhenUrlIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlValidator.validateUrl("");
        });
        assertEquals("The URL can't be empty", exception.getMessage());
    }

    @Test
    public void testWhenUrlIsInvalid() {
        String invalidUrl = "invalid-url";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlValidator.validateUrl(invalidUrl);
        });
        assertEquals("Invalid URL format: " + invalidUrl, exception.getMessage());
    }

    @Test
    public void testWhenUrlIsValid() {
        String validUrl = "http://example.com";
        assertDoesNotThrow(() -> {
            urlValidator.validateUrl(validUrl);
        });
    }
}
