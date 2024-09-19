package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequest {
    @NotBlank
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid URL format")
    private String url;
}
