package faang.school.urlshortenerservice.validator;

import org.springframework.stereotype.Component;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
@Component
public class UrlValidator {
    public void validate(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Неправильный url " + url);
        }
    }
}