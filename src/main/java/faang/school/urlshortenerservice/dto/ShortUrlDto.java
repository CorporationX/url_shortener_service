package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ShortUrlDto(
        @NotNull
        @NotEmpty
        String url
) {
}