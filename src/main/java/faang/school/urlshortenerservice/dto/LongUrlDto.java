package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LongUrlDto(@NotBlank(message = "URL must not be null or empty.")
                         @Pattern(regexp = "^https?://[^\\s]+$", message = "Invalid URL format") String longUrl) {
}
