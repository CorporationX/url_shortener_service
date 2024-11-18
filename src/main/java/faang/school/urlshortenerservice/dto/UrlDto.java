package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/[a-zA-Z0-9-._~:/?#@!$&'()*+,;=]*)?$",
            message = "Invalid URL")
    private String url;
}
