package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record CreateUrlDto(
        @NotBlank
        @URL(message = "Must be a valid URL")
        String targetUrl
) {
}
