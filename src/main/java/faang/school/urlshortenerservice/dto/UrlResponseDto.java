package faang.school.urlshortenerservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UrlResponseDto(
        String url,
        String hash,
        LocalDateTime createdAt
) {
}
