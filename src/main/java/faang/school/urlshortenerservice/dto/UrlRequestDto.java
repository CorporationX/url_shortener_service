package faang.school.urlshortenerservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class UrlRequestDto {
    @NotBlank(message = "URL не должен быть пустым")
    @Pattern(
            regexp = "^(https?://)?([\\w.-]+\\.[a-z]{2,6})([/\\w .-]*)*/?$",
            message = "Некорректный формат URL"
    )
    private String url;
}

