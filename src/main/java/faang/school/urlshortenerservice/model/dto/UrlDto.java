package faang.school.urlshortenerservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record UrlDto(
        @NotNull(message = "Url can't be null or empty!")
        @URL(message = "Invalid URL format!")
        String url
) {
}
