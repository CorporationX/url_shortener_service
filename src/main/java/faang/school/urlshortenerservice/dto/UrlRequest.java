package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlRequest {

    @NotBlank(message = "URL cannot be blank")
    @URL(message = "URL must be a valid URL")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "URL must be a valid HTTP/HTTPS URL"
    )
    private String url;
}
