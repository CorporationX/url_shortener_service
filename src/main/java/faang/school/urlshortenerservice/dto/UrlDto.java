package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UrlDto(
    @NotNull
    @Size(min = 8, max = 1024, message = "The URL length must be between 8 and 1024 characters.")
    String url
) {
}
