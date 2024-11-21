package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AppUrlValidatorTest {
    @InjectMocks
    private AppUrlValidator appUrlValidator;

    @Test
    void testValidate_Success() {
        String url1 = "https://faang-school.com/courses";
        String url2 = "https://www.google.com";
        List<String> validUrls = List.of(url1, url2);
        validUrls.forEach(url -> {
            assertDoesNotThrow(() -> appUrlValidator.validate(url));
        });
    }

    @Test
    void testValidate_Exception() {
        String url1 = "faang-school.com/courses";
        String url2 = "www.google.com";
        List<String> validUrls = List.of(url1, url2);
        validUrls.forEach(url -> {
            assertThrows(ValidationException.class, () -> {
                appUrlValidator.validate(url);
            });
        });
    }
}