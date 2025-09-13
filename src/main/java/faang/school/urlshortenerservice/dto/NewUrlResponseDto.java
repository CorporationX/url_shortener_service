package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record NewUrlResponseDto(
        @NotBlank
        @URL
        String shortUrl
) {
}
