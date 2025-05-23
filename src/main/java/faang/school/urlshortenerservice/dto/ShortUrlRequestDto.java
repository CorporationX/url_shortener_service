package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;

public record ShortUrlRequestDto(
        @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^ ]*$",
                message = "Invalid URL")
        String url) {
}
