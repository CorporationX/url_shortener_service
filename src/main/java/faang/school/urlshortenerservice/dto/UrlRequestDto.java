package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequestDto(
        @NotBlank(message = "URL cannot be null, empty or a space")
        @URL(message = "invalid URL")
        String url
) {}