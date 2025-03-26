package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlDto(
        @NotBlank(message = "Url can't be blank")
        @Pattern(regexp = "^(https?:\\/\\/)?([\\w\\-]+\\.)+[\\w\\-]{2,}(\\/\\S*)?$",
                message = "Invalid URL format")
        String url) {
}