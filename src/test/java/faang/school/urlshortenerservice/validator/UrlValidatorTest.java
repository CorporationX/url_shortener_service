package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataUrlValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {

    @InjectMocks
    private UrlValidator urlValidator;

    @Test
    public void testCheckIsNullHashIfNull() {
        assertThrows(DataUrlValidationException.class, () -> urlValidator.checkIsNullHash(null));
    }

    @Test
    public void testCheckIsNullHashIfBlank() {
        assertThrows(DataUrlValidationException.class, () -> urlValidator.checkIsNullHash("   "));
    }
}
