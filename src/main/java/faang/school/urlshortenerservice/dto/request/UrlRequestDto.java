package faang.school.urlshortenerservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlRequestDto(
    @NotBlank(message = "URL не должен быть пустым")
    @Pattern(
        regexp = "^https?://[\\w.-]+(:\\d+)?(/.*)?$",
        message = "Некорректный формат URL"
    )
    String url
) {}