package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UrlDto(

        @NotNull(message = "Url is null")
        @NotBlank(message = "Url is blank")
        @Pattern(regexp = "^https?://[a-zA-Z0-9\\-.]+\\.[a-zA-Z]{2,}(:\\d+)?(/[^\\s]*)?$",
                message = "This is not url")
        String url
) {
}
