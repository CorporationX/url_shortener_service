package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final HashRepository hashRepository;

    @CachePut(value = "#url.getLongUrl()") // Не понимаю, что нужно сюда написать(
    public String createShortLink(UrlDto url) {
        var hash = hashCache.getHash();
        return hashRepository.saveUrlAndHash(url.getLongUrl(), hash);
    }

    @Cacheable(value = "originUrl", key = "#url")
    public String getOriginUrl(String url) {
        log.info("Getting origin URL for: " + url);
        var originUrl = hashRepository.getOriginalUrl(url).orElseThrow(
                () -> new UrlNotFoundException("404", "Original URL not found for the given short URL")
        );
        return originUrl;
    }
}