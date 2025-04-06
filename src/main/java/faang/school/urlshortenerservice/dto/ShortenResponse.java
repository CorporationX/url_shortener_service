package faang.school.urlshortenerservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortenResponse {
    private String shortUrl;
}
