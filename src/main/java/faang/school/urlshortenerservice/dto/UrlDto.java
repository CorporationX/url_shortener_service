package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlDto {

    @Pattern(
            regexp = "^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]{1,5})?(/[^\\s]*)?$",
            message = "Received Url not valid"
    )
    private String url;
}
