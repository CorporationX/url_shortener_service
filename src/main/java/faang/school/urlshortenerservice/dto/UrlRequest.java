package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {
    @NotBlank(message = "URL must not be blank")
    @URL(message = "Invalid URL format")
    @Pattern(regexp = "https?://.+", message = "URL must start with http:// or https://")
    private String url;
}