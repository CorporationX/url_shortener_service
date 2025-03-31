package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record UrlRequestDto(
        @NotBlank(message = "URL не может быть пустым")
        @URL(message = "Некорректный формат URL")
        String originalUrl) {
}
