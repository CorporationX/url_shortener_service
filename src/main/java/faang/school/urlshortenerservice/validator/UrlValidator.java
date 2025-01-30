package faang.school.urlshortenerservice.validator;

import org.springframework.http.ResponseEntity;
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

    public void validateUrlForPutUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL не может быть пустым");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("Некорректный формат URL: " + url);
        }
    }
}
