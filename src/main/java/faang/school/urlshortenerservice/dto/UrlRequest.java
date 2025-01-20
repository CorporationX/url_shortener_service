package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequest {

    @NotBlank(message = "URL cannot be blank")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "URL must be a valid HTTP/HTTPS URL"
    )
    private String url;
}
