package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UrlRequestDto(
		@NotNull(message = "URL cannot be empty")
		@Pattern(regexp = "^(http|https)://.+", message = "Invalid URL format")
		String url
) {
}