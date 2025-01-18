package faang.school.urlshortenerservice.service.url_service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class UrlServiceProxy {

    private final UrlService urlService;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    public String getOriginalUrl(String hash) {
        log.info("Received request to get original URL for hash={}", hash);
        String originalUrl = urlService.getOriginalUrl(hash);
        urlCacheRepository.updateShortUrlRequestStats(hash);
        log.info("Found original URL={} for hash={}", originalUrl, hash);
        return originalUrl;
    }
}
