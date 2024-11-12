package faang.school.urlshortenerservice.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;

@Data
public class UrlRequest {
    @NotEmpty(message = "URL cannot be empty")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid URL format")
    private String longUrl;
}
