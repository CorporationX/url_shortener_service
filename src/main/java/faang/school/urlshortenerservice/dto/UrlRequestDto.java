package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlRequestDto(
        @NotBlank(message = "URL cannot be empty")
        @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "Invalid URL format")
        String longUrl
) {
}
