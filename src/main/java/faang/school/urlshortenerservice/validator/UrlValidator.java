package faang.school.urlshortenerservice.validator;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlValidator {
    public boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        try {
            new URI(value);
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }
}
