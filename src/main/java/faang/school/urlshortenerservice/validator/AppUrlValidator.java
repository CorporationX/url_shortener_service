package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.ValidationException;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

@Component
public class AppUrlValidator {
    public void validate(String url) {
        UrlValidator urlValidator = new UrlValidator();
        if (!urlValidator.isValid(url)) {
            throw new ValidationException("Url %s is not valid", url);
        }
    }
}
