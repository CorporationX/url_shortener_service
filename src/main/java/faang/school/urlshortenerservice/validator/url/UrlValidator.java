package faang.school.urlshortenerservice.validator.url;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class UrlValidator {

    public void validateUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("The URL can't be empty");
        }

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }
    }
}
