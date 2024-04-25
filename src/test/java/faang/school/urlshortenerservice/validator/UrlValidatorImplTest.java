package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlValidatorImplTest {

    private UrlValidatorImpl urlValidator;

    private final String hashPattern = "^[a-zA-Z0-9]{6}$";
    private final String urlPattern = "\\b(https?|ftp|file):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";

    @BeforeEach
    void setUp() {
        urlValidator = new UrlValidatorImpl();
        ReflectionTestUtils.setField(urlValidator, "hashPattern", hashPattern);
        ReflectionTestUtils.setField(urlValidator, "urlPattern", urlPattern);
    }

    @Test
    void validateHash_shouldPass_whenHashIsValid() {
        String validHash = "validH";
        String validHash2 = "aAaAaa";
        String validHash3 = "000000";
        String validHash4 = "123Abc";
        String validHash5 = "ZZZZZZ";

        assertDoesNotThrow(() -> urlValidator.validateHash(validHash));
        assertDoesNotThrow(() -> urlValidator.validateHash(validHash2));
        assertDoesNotThrow(() -> urlValidator.validateHash(validHash3));
        assertDoesNotThrow(() -> urlValidator.validateHash(validHash4));
        assertDoesNotThrow(() -> urlValidator.validateHash(validHash5));
    }

    @Test
    void validateUrl_shouldPass_whenUrlIsValid() {
        String validUrl = "https://www.google.com";
        String validUrl2 = "http://www.google.com";
        String validUrl3 = "ftp://www.google.com";
        String validUrl4 = "file://www.google.com";

        assertDoesNotThrow(() -> urlValidator.validateUrl(validUrl));
        assertDoesNotThrow(() -> urlValidator.validateUrl(validUrl2));
        assertDoesNotThrow(() -> urlValidator.validateUrl(validUrl3));
        assertDoesNotThrow(() -> urlValidator.validateUrl(validUrl4));
    }

    @Test
    void validateUrl_shouldThrowException_whenUrlIsInvalid() {
        String validUrl = "https://";
        String validUrl2 = "someNotValid";
        String validUrl3 = "12350";
        String validUrl4 = "port://www.google.com";

        assertThrows(DataValidationException.class, () -> urlValidator.validateUrl(validUrl));
        assertThrows(DataValidationException.class, () -> urlValidator.validateUrl(validUrl2));
        assertThrows(DataValidationException.class, () -> urlValidator.validateUrl(validUrl3));
        assertThrows(DataValidationException.class, () -> urlValidator.validateUrl(validUrl4));
    }

    @Test
    void validateHash_shouldThrowException_whenHashIsInvalid() {
        String validHash = "+alidH";
        String validHash2 = ".!aAaa";
        String validHash3 = "0000000";
        String validHash4 = "------";
        String validHash5 = "";
        String validHash6 = "  ";
        String validHash7 = "______";

        assertThrows(DataValidationException.class, () -> urlValidator.validateHash(validHash));
        assertThrows(DataValidationException.class, () -> urlValidator.validateHash(validHash2));
        assertThrows(DataValidationException.class, () -> urlValidator.validateHash(validHash3));
        assertThrows(DataValidationException.class, () -> urlValidator.validateHash(validHash4));
        assertThrows(DataValidationException.class, () -> urlValidator.validateHash(validHash5));
        assertThrows(DataValidationException.class, () -> urlValidator.validateHash(validHash6));
        assertThrows(DataValidationException.class, () -> urlValidator.validateHash(validHash7));
    }

}