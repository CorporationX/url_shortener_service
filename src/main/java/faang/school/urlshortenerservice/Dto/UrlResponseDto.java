package faang.school.urlshortenerservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponseDto {
    private String shortUrl;
    private String originalUrl;
    private String hash;
}
