package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlDto {
    @NotEmpty(message = "URL cannot be empty")
    @Pattern(
            regexp = "^(?i)(http|https)://.*$",
            message = "URL must start with http:// or https://"
    )
    private String url;
    private String shortUrl;
}