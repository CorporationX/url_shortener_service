package faang.school.urlshortenerservice.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlDto {
    @NotEmpty
//    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid URL format")
    private String url;

    private LocalDateTime createdAt;
}
