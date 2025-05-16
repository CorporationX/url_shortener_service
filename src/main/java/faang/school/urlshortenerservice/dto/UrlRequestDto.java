package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^https?://([\\w-]+\\.)+[\\w-]+(:\\d+)?(/[\\w\\-./?%&=]*)?$", message = "Invalid URL format")
    private String url;
}
