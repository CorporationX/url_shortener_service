package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlRequestDto(
        @NotBlank(message = "URL cannot be empty")
        @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$", message = "Invalid URL format")
        String longUrl
) {
}
