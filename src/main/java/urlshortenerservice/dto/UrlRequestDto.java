package urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UrlRequestDto {
    @NotNull(message = "Url can't be null")
    private String url;
}
