package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UrlDto {
    @Pattern(regexp = "^(https?)://[^\\s/$.?#].[^\\s]*$")
    private String url;
}
