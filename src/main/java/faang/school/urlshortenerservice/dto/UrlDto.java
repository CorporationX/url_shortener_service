package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    @NotNull(message = "URL cannot be null")
    @URL(regexp = "^((https?|ftp):\\/\\/)?([0-9a-zA-Z\\-\\.]+)(?::(\\d+))?(\\/[^?#]*)?(?:\\?([^#]*))?(?:#(.*))?$")
    private String baseUrl;

}