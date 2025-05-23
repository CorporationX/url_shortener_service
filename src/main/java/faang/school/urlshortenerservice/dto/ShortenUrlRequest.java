package faang.school.urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Schema(description = "Request to shorten a URL")
public record ShortenUrlRequest(
        @NotBlank(message = "Original URL must be provided.")
        @URL(message = "Provided string is not a valid URL.")
        @Size(max = 2048, message = "URL must be less than 2048 characters")
        String originalUrl
) {
}