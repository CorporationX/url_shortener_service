package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record UrlRequestDto(
        String url
) {
}
