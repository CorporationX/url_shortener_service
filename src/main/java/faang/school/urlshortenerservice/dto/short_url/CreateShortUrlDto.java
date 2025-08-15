package faang.school.urlshortenerservice.dto.short_url;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record CreateShortUrlDto(
        @NotBlank(message = "Url is required")
        @URL(message = "Must be url")
        @Schema(description = "Target URL to generate a short link for")
        String originalUrl
) {
}
