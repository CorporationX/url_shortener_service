package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlValidator {
    public void validateUrl(String url) {
        try {
            new URI(url);
        } catch (URISyntaxException e) {
            throw new DataValidationException("Passed url isn't valid. Please check your url.");
        }
    }
}