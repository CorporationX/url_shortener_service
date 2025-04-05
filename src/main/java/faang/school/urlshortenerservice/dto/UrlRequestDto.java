package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlRequestDto {
    @NotNull(message = "URL cannot be empty")
    @URL
    @Pattern(regexp = "^(http|https)://.*$", message = "URL must start with http:// or https://")
    private String originalUrl;
}
