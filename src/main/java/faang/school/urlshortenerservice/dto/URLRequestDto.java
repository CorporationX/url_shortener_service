package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record URLRequestDto(
        @NotBlank(message = "URL cannot be blank")
        @Size(min = 5, max = 2000, message = "URL must be between 5 and 2000 characters")
        String url
) {
}
