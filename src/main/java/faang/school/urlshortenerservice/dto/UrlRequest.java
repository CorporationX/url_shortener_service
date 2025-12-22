package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;

public record UrlRequest(
        @URL(message = "Invalid URL format")  // ← лучше с сообщением
        String url
) {
}