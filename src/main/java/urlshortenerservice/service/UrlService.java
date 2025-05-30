package urlshortenerservice.service;

import urlshortenerservice.dto.UrlResponseDto;
import org.springframework.stereotype.Component;

@Component
public interface UrlService {
    String getOriginalUrl(String hash);

    UrlResponseDto createShortUrl(String originalUrl);
}
