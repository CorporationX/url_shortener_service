package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LongUrlDto(@NotBlank(message = "URL must not be null or empty.")
                         @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9+.-]*:(\\S*)$", message = "Invalid URL format") String longUrl) {
}
