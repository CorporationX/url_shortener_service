package faang.school.urlshortenerservice.validation.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UrlValidatorTest {

    @InjectMocks
    private UrlValidator urlValidator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        String urlRegex = "^https?://[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$";
        ReflectionTestUtils.setField(urlValidator, "urlRegex", urlRegex);
        urlValidator.initialize(null);
    }

    @ParameterizedTest
    @CsvSource({
            "https://example.com, true",
            "http://sub.domain.com/path, true",
            "ftp://example.com, false",
            "invalid-url, false",
            "example.com, false",
            "http://, false",
            "https://example.com?query=1, true"
    })
    void testUrlValidation(String url, boolean expectedResult) {
        boolean isValid = urlValidator.isValid("null".equals(url) ? null : url, context);

        assertEquals(expectedResult, isValid);
    }
}