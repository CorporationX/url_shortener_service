package faang.school.urlshortenerservice.validator;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlServiceValidator {
    public void checkUrl(String url) {
        try {
            URL netUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url");
        }

    }
}
