package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RequestUrl {
    @Pattern(regexp = "^https?://.*", message = "Invalid URL")
    private String url;

}
