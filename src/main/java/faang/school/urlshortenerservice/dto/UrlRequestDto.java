package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequestDto {
    @NotEmpty(message = "URL cannot be empty")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid URL format")
    private String longUrl;

    public UrlRequestDto() {
    }
}
