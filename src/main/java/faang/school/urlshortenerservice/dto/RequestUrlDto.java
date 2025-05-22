package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestUrlDto {
    @NotBlank(message = "url should not be null of blank")
    private String url;
}
