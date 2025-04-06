package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exceptions.InvalidUrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Component
@Slf4j
public class UrlValidator {
    public void validateUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException e) {
            log.error("Malformed URL: {}", url, e);
            throw new InvalidUrlException("Malformed URL: " + url);
        } catch (URISyntaxException e) {
            log.error("URI Syntax Error: {}", url, e);
            throw new InvalidUrlException("URI Syntax Error: " + url);
        }
    }
}