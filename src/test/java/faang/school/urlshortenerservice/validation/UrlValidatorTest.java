package faang.school.urlshortenerservice.validation;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {

    private final UrlValidator urlValidator = new UrlValidator();

    @Test
    @DisplayName("Should not throw exception for valid URL")
    public void testValidateUrl_ValidUrl() {
        String validUrl = "https://www.example.com";

        assertDoesNotThrow(() -> urlValidator.validateUrl(validUrl));
    }

    @Test
    @DisplayName("Should throw DataValidationException for invalid URL")
    public void testValidateUrl_InvalidUrl() {
        String invalidUrl = "invalid-url";

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            urlValidator.validateUrl(invalidUrl);
        });

        assertEquals("Please provide a valid URL", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for URL without scheme")
    public void testValidateUrl_UrlWithoutScheme() {
        String urlWithoutScheme = "www.example.com";

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            urlValidator.validateUrl(urlWithoutScheme);
        });

        assertEquals("Please provide a valid URL", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty URL")
    public void testValidateUrl_EmptyUrl() {
        String emptyUrl = "";

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            urlValidator.validateUrl(emptyUrl);
        });

        assertEquals("Please provide a valid URL", exception.getMessage());
    }
}