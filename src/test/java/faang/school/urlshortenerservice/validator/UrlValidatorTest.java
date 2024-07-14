package faang.school.urlshortenerservice.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {

    private final UrlValidator urlValidator = new UrlValidator();

    @Test
    void testValidateUrl() {
        String url = "https://anyurl/test";

        assertDoesNotThrow(() -> urlValidator.validateUrl(url));
    }

    @Test
    void testValidateUrlThrowException() {
        String url = "anyurl";

        assertThrows(IllegalArgumentException.class,
                () -> urlValidator.validateUrl(url));
    }
}
