package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @Pattern(
            regexp = "^(https?://)" +
                    "(([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)" +
                    "{3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(\\:[0-9]{1,5})?(/.*)?$",
            message = "Invalid url"
    )
    private String url;
}
