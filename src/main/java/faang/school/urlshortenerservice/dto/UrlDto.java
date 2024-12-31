package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @NotBlank(message = "url cannot be empty")
    @Pattern(regexp = "^(https?://)?([\\w.-]+)\\.([a-z\\.]{2,6})([/\\w.-]*)*/?$",
             message = "Invalid URL format")
    private String url;
}