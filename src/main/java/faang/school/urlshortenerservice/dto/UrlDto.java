package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlDto {

    private String hash;

    @NotNull(message = "Url cannot be null")
    @NotEmpty(message = "Url cannot be empty")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid Url format")
    private String url;
}
