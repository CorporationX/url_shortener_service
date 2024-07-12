package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.UrlException;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Component
public class UrlValidator {

    public void validateUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e ) {
            throw new UrlException("invalid url passed");
        }
    }
}
