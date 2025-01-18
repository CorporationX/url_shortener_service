package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UrlDto(
        @NotBlank(message = "URL cannot be empty")
        String url) {
}
