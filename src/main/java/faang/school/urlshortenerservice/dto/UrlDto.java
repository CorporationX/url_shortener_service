package faang.school.urlshortenerservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UrlDto(
        String hash,
        String url,
        LocalDateTime createdAt
) {
}
