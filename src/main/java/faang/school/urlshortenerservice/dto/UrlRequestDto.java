package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UrlRequestDto(
        @NotBlank(message = "URL не может быть пустым")
        @Pattern(
                regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
                message = "Некорректный формат URL"
        )
        String originalUrl) {
}
