package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    @Pattern(
            regexp = "${url.original.patterns}",
            message = "Invalid URL format"
    )
    private String url;
    private String hash;
    private LocalDateTime createdAt;
}
