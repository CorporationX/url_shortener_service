package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UrlDto {
    @NotEmpty
    private String url;
}