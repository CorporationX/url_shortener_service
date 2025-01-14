package faang.school.urlshortenerservice.validator;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
@Component
public class UrlServiceValidator {
    public void checkUrl(String url) {
        try {
            URL netUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url");
        }

    }
}
