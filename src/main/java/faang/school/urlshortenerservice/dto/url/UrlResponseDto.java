package faang.school.urlshortenerservice.dto.url;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UrlResponseDto {
    private String url;
    private String hash;
    private LocalDateTime createdAt;
}
