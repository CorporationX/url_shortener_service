package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequestDto(
        @NotBlank
        @URL
        String originalUrl) {
}