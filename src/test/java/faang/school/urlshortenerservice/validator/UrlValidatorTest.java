package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exceptions.InvalidUrlException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlValidatorTest {

    private final UrlValidator urlValidator = new UrlValidator();

    @Test
    void validateUrl_WithValidUrl_ShouldNotThrowException() {
        urlValidator.validateUrl("https://www.example.com");
    }

    @Test
    void validateUrl_WithMalformedUrl_ShouldThrowInvalidUrlException() {
        assertThrows(InvalidUrlException.class, () -> urlValidator.validateUrl("htp://invalid-url"));
    }

    @Test
    void validateUrl_WithUriSyntaxError_ShouldThrowInvalidUrlException() {
        assertThrows(InvalidUrlException.class, () -> urlValidator.validateUrl("http://invalid-url|example.com"));
    }
}