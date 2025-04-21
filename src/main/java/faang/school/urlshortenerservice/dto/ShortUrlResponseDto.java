package faang.school.urlshortenerservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ShortUrlResponseDto {

    private final String shortUrl;
}
