package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlCreateDto {
    @NotBlank(message = "URL не может быть пустым")
    @Pattern(
            regexp = "^(https?://)([\\w.-]+)(:[0-9]+)?(/.*)?$",
            message = "Некорректный формат URL"
    )
    private String originalUrl;
}
