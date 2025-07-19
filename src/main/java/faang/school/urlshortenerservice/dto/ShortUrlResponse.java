package faang.school.urlshortenerservice.dto;

import java.time.LocalDateTime;

public record ShortUrlResponse(
        String shortUrl,
        LocalDateTime expirationTime
) {
}
