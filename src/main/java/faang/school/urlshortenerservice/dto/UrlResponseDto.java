package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UrlResponseDto {
    @NotBlank(message = "URL cannot be null or blank")
    @Pattern(
            regexp = "^(?i)(http|https)://.*$",
            message = "URL must start with http:// or https://"
    )
    private String shortUrl;
}