package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.UrlValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Component
public class UrlValidator {
    public void validateUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            log.error("Invalid URL", e);
            throw new UrlValidationException("Invalid URL");
        }
    }
}
