package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be empty")
    @URL(message = "Invalid URL format")
    private String url;
}
