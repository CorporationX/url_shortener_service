package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlRequest {
    @NotBlank(message = "URL must not be blank")
    @URL(message = "Invalid URL format")
    private String url;
}
