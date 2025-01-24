package faang.school.urlshortenerservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
@Slf4j
public class UrlServiceValidator {

    public void validateHash(String hash) {
        if (hash == null || hash.isBlank() || hash.length() > 7) {
            log.warn("hash was null or blank or with incorrect length");
            throw new IllegalArgumentException("incorrect given hash: " + hash);
        }
    }
}
