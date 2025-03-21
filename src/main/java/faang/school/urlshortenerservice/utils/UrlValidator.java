package faang.school.urlshortenerservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UrlValidator {

    public void isUrl(String url) {
        log.info("url validating");

        String regex = "^https?:\\/\\/.+\\..+$";

        if (!url.matches(regex)) {
            throw new IllegalArgumentException("This is not a link");
        }
    }
}
