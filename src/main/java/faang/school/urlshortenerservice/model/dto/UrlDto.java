package faang.school.urlshortenerservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record UrlDto(
        @NotNull(message = "URL can not be null")
        @URL(message = "URL must start with http or https")
        String url) {
}
