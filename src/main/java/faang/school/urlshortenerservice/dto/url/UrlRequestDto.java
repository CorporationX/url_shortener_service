package faang.school.urlshortenerservice.dto.url;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlRequestDto (@NotBlank(message = "URL is empty")
                             @Pattern(
                                     regexp = "^(http|https)://.*$",
                                     message = "URL must start with http:// or https://"
                             )
                             String url) {
}
