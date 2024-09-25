package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @NotEmpty(message = "Url is empty")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid URL format")
    private String url;
    private LocalDateTime createdAt;
}
