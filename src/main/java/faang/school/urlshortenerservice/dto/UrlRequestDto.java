package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record UrlRequestDto(
        @NotNull
                @URL
        String url
) {
}
