package faang.school.urlshortenerservice.model.dto.request.url;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShortenRequest {
    @NotBlank(message = "URL must not be null or empty")
    private String url;
}
