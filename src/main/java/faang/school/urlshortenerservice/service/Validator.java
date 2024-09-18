package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import org.springframework.stereotype.Component;

@Component
public class Validator {
    public void validateUrl(UrlDto url) {
        if (!url.url().toLowerCase().startsWith("https://")) {
            throw new IllegalArgumentException("Не корректный URL");
        }
    }
}
