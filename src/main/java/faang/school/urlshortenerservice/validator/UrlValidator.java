package faang.school.urlshortenerservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@Component
public class UrlValidator {
    public void isValid(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException ignored) {
            log.info("Incorrect URL passed: " + url);
        }
    }
}
