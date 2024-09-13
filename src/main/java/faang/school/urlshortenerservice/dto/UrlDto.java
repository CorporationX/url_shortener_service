package faang.school.urlshortenerservice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Builder
public record UrlDto(
        @Nullable
        String hash,
        @URL(message = "Invalid URL")
        @NotBlank(message = "Url is mandatory")
        String url,
        @Nullable
        LocalDateTime createdAt
) {}
