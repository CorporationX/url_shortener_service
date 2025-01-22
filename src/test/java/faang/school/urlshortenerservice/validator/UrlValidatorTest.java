package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {

    @InjectMocks
    private UrlValidator urlValidator;

    @Mock
    private UrlRepository urlRepository;

    @Test
    void testValidateUrlWithException() {
        assertFalse(() -> urlValidator.isValidUrl("hello"));
    }

    @Test
    void testValidateUrlWithNoException() {
        assertTrue(() -> urlValidator.isValidUrl("https://www.google.com"));
    }

}
