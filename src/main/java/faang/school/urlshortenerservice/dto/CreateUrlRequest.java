package faang.school.urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record CreateUrlRequest(

        @NotBlank(message = "URL is required")
        @URL(message = "Must be a valid URL")
        @Schema(
                description = "Original long URL to shorten",
                example = "https://example.com/products/12345"
        )
        String url
) {
}
