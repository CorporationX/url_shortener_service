package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.net.URI;

@Builder
public record UrlResponse(
        @NotBlank
        URI url
) {
}
