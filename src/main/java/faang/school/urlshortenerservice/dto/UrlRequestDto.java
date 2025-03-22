package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlRequestDto {
    @NotNull(message = "URL cannot be empty")
    @URL
    private String originalUrl;
}
