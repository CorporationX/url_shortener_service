package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataUrlValidationException;
import org.springframework.stereotype.Component;

@Component
public class UrlValidator {

    public void checkIsNullHash(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new DataUrlValidationException("Argument hash must not be null or empty");
        }
    }
}
