package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
public class UrlRequestDto {
    @NotEmpty(message = "URL cannot be empty")
    @URL(message = "Invalid URL format")
    @Size(max = 256)
    private String longUrl;
}
