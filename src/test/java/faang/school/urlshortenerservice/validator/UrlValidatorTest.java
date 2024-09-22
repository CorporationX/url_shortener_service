package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {
    @InjectMocks
    private UrlValidator urlValidator;

    @Test
    public void testValidateNull() {
        Assertions.assertThrows(DataValidationException.class, () -> urlValidator.validate(null));
    }

    @Test
    public void testValidateNotUrl() {
        Assertions.assertThrows(DataValidationException.class, () -> urlValidator.validate("https://google com/"));
    }

    @Test
    public void testValidate() {
        urlValidator.validate("https://google.com/");
    }
}
