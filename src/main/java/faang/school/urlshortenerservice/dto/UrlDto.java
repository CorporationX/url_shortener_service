package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @Pattern(regexp = "^(https?)://[^\\s/$.?#].[^\\s]*$")
    private String url;
}
