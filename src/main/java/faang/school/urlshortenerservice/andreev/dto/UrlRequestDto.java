package faang.school.urlshortenerservice.andreev.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UrlRequestDto {
    @NotNull(message = "Url can't not be null")
    private String url;
}
