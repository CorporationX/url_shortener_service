package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlDto {

    @Pattern(
            regexp = "^(https://|http://)([\\w.-]+)+(:\\d+)?(/\\S+)?$",
            message = "Passed string is not valid Url"
    )
    private String url;
}