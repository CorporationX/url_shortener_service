package faang.school.urlshortenerservice.model.dto.response.url;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortenResponse {
    private String shortUrl;
}
