package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequestDto {

    @NotBlank(message = "URL не должен быть пустым")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Некорректный URL. Он должен начинаться с http:// или https://"
    )
    private String originalUrl;
}