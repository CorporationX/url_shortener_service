package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(http|https)://.*$", message = "This is not URL")
    private String url;

}
