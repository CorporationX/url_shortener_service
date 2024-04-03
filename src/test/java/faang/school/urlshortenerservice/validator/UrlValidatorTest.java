package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlValidatorTest {

    @Test
    public void testValidateUrlWhenNullUrl() {
        UrlValidator urlValidator = new UrlValidator();
        String nullUrl = null;

        assertThrows(DataValidationException.class, () -> urlValidator.validateUrl(nullUrl));
    }

    @Test
    public void testValidateUrlWhenEmptyUrl() {
        UrlValidator urlValidator = new UrlValidator();
        String emptyUrl = "";

        assertThrows(DataValidationException.class, () -> urlValidator.validateUrl(emptyUrl));
    }

    @Test
    public void testValidateUrlWhenInvalidUrl() {
        UrlValidator urlValidator = new UrlValidator();
        String invalidUrl = "invalidUrl";

        assertThrows(DataValidationException.class, () -> urlValidator.validateUrl(invalidUrl));
    }

    @Test
    public void testValidateSearchUrlWhenSearchUrlEntityIsNull() {
        UrlValidator urlValidator = new UrlValidator();
        UrlEntity searchUrlEntity = null;
        String hash = "123456";

        assertThrows(UrlNotFoundException.class, () -> urlValidator.validateSearchUrl(searchUrlEntity, hash));
    }

    @Test
    public void testValidateSearchUrlWhenUrlInSearchUrlEntityIsNull() {
        UrlValidator urlValidator = new UrlValidator();
        UrlEntity searchUrlEntity = new UrlEntity();
        searchUrlEntity.setUrl(null);
        String hash = "123456";

        assertThrows(UrlNotFoundException.class, () -> urlValidator.validateSearchUrl(searchUrlEntity, hash));
    }

    @Test
    public void testValidateSearchUrlWhenSearchUrlEntityIsValid() {
        UrlValidator urlValidator = new UrlValidator();
        UrlEntity searchUrlEntity = new UrlEntity();
        searchUrlEntity.setUrl("https://www.google.com");
        String hash = "123456";

        assertDoesNotThrow(() -> urlValidator.validateSearchUrl(searchUrlEntity, hash));
    }
}