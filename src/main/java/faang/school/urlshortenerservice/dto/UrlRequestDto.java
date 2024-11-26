package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlRequestDto {
    @NotNull(message = "URL cannot be null")
    @URL(message = "Invalid URL format")
    @Size(max = 1000, message = "URL cannot be longer than 1000 characters")
    private String url;
}
