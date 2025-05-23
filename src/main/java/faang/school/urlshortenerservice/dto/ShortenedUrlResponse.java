package faang.school.urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Shortened URL response")
public record ShortenedUrlResponse(
        String url
) {
}