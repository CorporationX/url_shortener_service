package faang.school.urlshortenerservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UrlResponseDto {
    private final String shortUrl;
}
