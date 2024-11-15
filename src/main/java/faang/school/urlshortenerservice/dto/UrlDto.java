package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.validator.annotaiton.Url;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UrlDto {
    @NotNull
    @Url
    private String url;
}
