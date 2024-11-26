package faang.school.urlshortenerservice.dto.response;

import lombok.Builder;

@Builder
public record UrlResponse(String shortUrl) {
}