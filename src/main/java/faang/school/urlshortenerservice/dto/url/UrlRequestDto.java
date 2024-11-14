package faang.school.urlshortenerservice.dto.url;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record UrlRequestDto(
        @NotNull(message = "Url should not be null")
        @NotEmpty(message = "Url should not be empty")
        @URL(message = "URL format incorrect")
        String url
) {
}
