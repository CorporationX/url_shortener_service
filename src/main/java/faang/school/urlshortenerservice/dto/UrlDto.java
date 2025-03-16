package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlDto {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(http?://).+", message = "Invalid URL format")
    private String url;
}
