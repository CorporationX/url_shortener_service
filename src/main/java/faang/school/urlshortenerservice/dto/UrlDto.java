package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    private String hash;

    @Pattern(regexp = "^(https?)://[^\s/$.?#].[^\s]*$", message = "Invalid URL format")
    private String url;
}
