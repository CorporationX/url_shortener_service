package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(https?|ftp)://([^\\s/$.?#]\\S*)$", message = "Invalid URL format. Valid examples: http://example.com, https://sub.example.com/path")
    private String url;
}
