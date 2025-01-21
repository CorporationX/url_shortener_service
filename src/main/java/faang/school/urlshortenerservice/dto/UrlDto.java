package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UrlDto {
    @NotNull(message = "URL must not be null")
    @Size(max = 1024, message = "URL must contain a maximum size of 1024 characters")
    private String originalUrl;
}