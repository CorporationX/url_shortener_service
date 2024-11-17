package faang.school.urlshortenerservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class UrlRequestDto {

    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(https?|ftp)://[\\w-]+(\\.[\\w-]+)+(/[^\\s]*)?$", message = "Invalid URL format")
    private String originalUrl;
}