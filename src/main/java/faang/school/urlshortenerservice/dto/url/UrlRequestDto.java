package faang.school.urlshortenerservice.dto.url;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(
            regexp = "^(https?|ftp)://([^\\s/$.?#]+|localhost|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})[^\\s]*$",
            message = "Invalid URL format"
    )
    private String url;
}
