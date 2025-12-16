package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record CreateUrlResponseDto(
        String shortUrl
) {
}
