package faang.school.urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Setter
public class UrlDto {
    @Schema(description = "Url link", example = "https://sample.url")
    @NotBlank(message = "URL cannot be empty")
    @URL(message = "Invalid URL format")
    private String url;

    @Schema(description = "Shortened URL hash", example = "a1b2c3")
    private String hash;

    @Schema(description = "Identifier of User requesting for short Url", example = "1")
    @NotNull(message = "User ID cannot be empty")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @Schema(description = "Local Date Time stamp for request")
    private LocalDateTime createdAt;
}
