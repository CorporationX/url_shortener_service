package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record UrlResponseDto(String originalUrl, String shortUrl) {
}
