package faang.school.urlshortenerservice.urlValidation;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import static faang.school.urlshortenerservice.exception.ExceptionMessage.WRONG_LINK_FORMAT;

@Component
public class URLValidator {

    public void isValidURL(String url) {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https", "ftp"});
        if (!urlValidator.isValid(url)) {
            throw new DataValidationException(WRONG_LINK_FORMAT.getMessage());
        }
    }
}
