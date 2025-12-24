package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Builder
public record UrlDto(
        String hash,
        @NotNull(message = "The Url must exist")
        @NotBlank(message = "The Url cannot be blank")
        @URL(message = "The incoming string is not the Url")
        String url,
        LocalDateTime createdAt
) {
}
