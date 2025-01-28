package faang.school.urlshortenerservice.validator;

import org.springframework.stereotype.Component;

@Component
public class UrlValidator {
    public void validateUrl(String originalUrl, String hash) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new IllegalArgumentException("URL не найден для hash: " + hash);
        }

        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            throw new IllegalStateException("Некорректный формат URL: " + originalUrl);
        }
    }
}
