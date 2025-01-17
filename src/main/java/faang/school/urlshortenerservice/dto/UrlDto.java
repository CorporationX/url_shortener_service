package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "URL can not be null")
    @Pattern(regexp = "^(http|https)://.*$", message = "URL must start with http or https")
    private String url;
}
