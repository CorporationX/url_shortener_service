package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;

public interface UrlService {
    UrlResponseDto createShortUrl(UrlRequestDto urlRequestDto);

    UrlResponseDto getOriginalUrl(String hash);
}
