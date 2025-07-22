package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;

public record OriginalUrl(
        @NotBlank(message = "URL cannot be null or empty")
        String url) {
}
