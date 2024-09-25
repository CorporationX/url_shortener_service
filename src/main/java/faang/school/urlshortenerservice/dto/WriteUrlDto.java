package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WriteUrlDto {
    @NotBlank(message = "URL must not be blank")
    @Pattern(regexp = "^(http|https)://.*$", message = "URL must start with http:// or https://")
    private String url;
}