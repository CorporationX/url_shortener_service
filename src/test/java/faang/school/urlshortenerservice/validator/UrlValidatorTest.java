package faang.school.urlshortenerservice.validator;

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
        String urlPattern = "^https?://[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$";
        ReflectionTestUtils.setField(urlValidator, "urlPattern", urlPattern);
    }

    @ParameterizedTest
    @CsvSource({
            "https://example.com, true",
            "invalid-url, false",
            "null, false",
    })
    void testUrlValidation(String url, boolean expectedResult) {
        boolean valid = urlValidator.isValid("null".equals(url) ? null : url, context);

        assertEquals(expectedResult, valid);
    }
}