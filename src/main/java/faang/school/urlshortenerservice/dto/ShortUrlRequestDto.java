package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ShortUrlRequestDto(
        @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^ ]*$",
                message = "Invalid or empty URL")
        @NotNull(message = "URL is required")
        String url) {
}
