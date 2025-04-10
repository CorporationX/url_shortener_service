package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record LongUrlDto(
        @NotBlank(message = "URL cannot be empty")
        @URL(message = "Invalid URL format")
        String url
) {
}