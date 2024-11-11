package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AppUrlValidatorTest {
    @InjectMocks
    private AppUrlValidator appUrlValidator;

    @Test
    void testValidate_Success() {
        doesntThrowsException("http://faang-school.com/courses");
        doesntThrowsException("https://faang-school.com/courses");
        doesntThrowsException("https://www.faang-school.com/courses");
    }

    @Test
    void testValidate_Exception() {
        throwsException("www.faang-school.com/courses");
        throwsException("https://faang-school");
        throwsException("faang-school.com/courses");
    }

    private void doesntThrowsException(String url) {
        assertDoesNotThrow(() ->
                appUrlValidator.validate(url)
        );
    }

    private void throwsException(String url) {
        assertThrows(ValidationException.class, () ->
                appUrlValidator.validate(url)
        );
    }
}