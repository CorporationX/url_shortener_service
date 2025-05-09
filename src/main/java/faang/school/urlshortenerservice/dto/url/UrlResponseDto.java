package faang.school.urlshortenerservice.dto.url;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlResponseDto {
    private String url;
    private String hash;
}
