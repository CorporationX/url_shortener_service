package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    private static final String REGEXP_FORMAT = "^(https?://)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*$";

    @NotBlank(message = "URL cannot be empty")
    @Pattern(
            regexp = REGEXP_FORMAT,
            message = "Invalid URL format"
    )
    private String url;
    private String hash;
    private LocalDateTime createdAt;
}
