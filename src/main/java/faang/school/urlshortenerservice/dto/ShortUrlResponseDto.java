package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record ShortUrlResponseDto(

        String shortUrl,
        String hash,
        String originalUrl
) {}
