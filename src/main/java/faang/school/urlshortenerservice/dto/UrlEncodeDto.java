package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UrlEncodeDto(
    @NotBlank(message = "url is empty")
    String url
) {
}
