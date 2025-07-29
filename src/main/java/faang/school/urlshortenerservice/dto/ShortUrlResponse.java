package faang.school.urlshortenerservice.dto;

import java.net.URI;
import java.time.LocalDateTime;

public record ShortUrlResponse(
        URI shortUrl,
        LocalDateTime expirationTime
) {
}
