package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
public class UrlShortenerDto {
    @NotBlank(message = "URL must not be empty")
    @URL(message = "Must be a valid URL (including http/https)")
    private String longUrl;
}
