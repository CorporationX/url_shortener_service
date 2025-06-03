package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(@NotBlank
                         @URL(message = "Некорректный формат URL")
                         String originalUrl) {
}
