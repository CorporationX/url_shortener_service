package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(
        @NotBlank(message = "The URL must not be empty.")
        @URL(message = "Invalid URL format")
        String url
) {
}
