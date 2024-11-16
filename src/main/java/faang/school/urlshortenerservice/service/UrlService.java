package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    public String generateShortUrl(UrlDto url) {
        String shorUrl = hashCache.getHash();
        Url modelUrl = Url.builder()
            .hash(shorUrl)
            .url(url.url())
            .createdAt(LocalDateTime.now()) // сам spring значение не подставляет почему-то
            .build();
        urlRepository.save(modelUrl);

        log.info("Url been generated: {}", modelUrl);
        return shorUrl;
    }

    @Cacheable("hash")
    public String getUrl(String shortUrl) {
        String url = urlRepository.findByShortUrl(shortUrl).orElseThrow(() -> {
            String message = "short link not found for url: %s".formatted(shortUrl);
            return new RuntimeException(message);
        });
        log.info("Link {} was issued for hash {}", url, shortUrl);
        return url;
    }
}
