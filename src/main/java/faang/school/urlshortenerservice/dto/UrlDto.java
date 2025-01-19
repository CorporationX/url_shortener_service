package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlDto(
        @NotBlank(message = "URL must not be null or blank")
        @Pattern(regexp = "^(https?://)?([a-zA-Z0-9\\-._~%]+@)?([a-zA-Z0-9\\-._~%]+\\.[a-zA-Z]{2,})(:[0-9]{1,5})?(/\\S*)?$",
                message = "URL must start with 'http://' or 'https://'")
        String url) {
}
