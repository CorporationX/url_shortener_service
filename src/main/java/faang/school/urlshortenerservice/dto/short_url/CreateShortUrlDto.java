package faang.school.urlshortenerservice.dto.short_url;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record CreateShortUrlDto(
        @NotBlank(message = "URL is required")
        @URL(message = "Must be a valid URL")
        @Schema(description = "Target URL to generate a short link for (must start with http/https)")
        String url
) {
}