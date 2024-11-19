package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlGenerator {

    @Value("${app.short-url-pattern}")
    private String shortUrlPattern;

    public String makeShortUrl(String hash) {
        return shortUrlPattern + hash;
    }
}
