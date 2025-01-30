package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ShortUrlDto(
        @NotBlank String shortUrl
) {
}
