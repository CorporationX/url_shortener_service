package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UrlDto {
    @NotNull
    private String url;
}
