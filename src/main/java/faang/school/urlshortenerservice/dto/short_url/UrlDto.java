package faang.school.urlshortenerservice.dto.short_url;

import jakarta.validation.constraints.NotBlank;

import java.net.URI;

public record UrlDto(
        @NotBlank
        URI url
) {

}
