package faang.school.urlshortenerservice.dto;

import java.time.LocalDateTime;

public record UrlDto(
        String hash,
        String url,
        LocalDateTime createdAt
) {
}
