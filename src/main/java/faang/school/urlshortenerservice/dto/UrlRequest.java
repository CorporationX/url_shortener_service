package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequest {
    @NotBlank(message = "URL must not be blank")
    @Pattern(regexp = "https?://.+", message = "Invalid URL format")
    private String url;
}
