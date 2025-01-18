package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {

    @InjectMocks
    private UrlValidator urlValidator;

    @Mock
    private UrlRepository urlRepository;

    @Test
    void testValidateUrlWithException() {
        assertThrows(DataValidationException.class, () -> urlValidator.validateUrl("hello"));
    }

    @Test
    void testValidateUrlWithNoException() {
        assertDoesNotThrow(() -> urlValidator.validateUrl("https://www.google.com"));
    }

}
