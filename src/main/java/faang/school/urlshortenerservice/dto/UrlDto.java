package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UrlDto(@NotBlank(message = "URL cannot be null or empty")
                     @Pattern(regexp = "https?://.*", message = "Invalid URL format") String url) {
}
