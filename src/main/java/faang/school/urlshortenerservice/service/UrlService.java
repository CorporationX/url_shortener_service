package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    // private final UrlCacheRepository  urlCacheRepository;

    public String generateShortUrl(UrlDto url) {
        String shorUrl = hashCache.getHash();
        Url modelUrl = Url.builder()
            .hash(shorUrl)
            .url(url.url())
            .createdAt(LocalDateTime.now()) // сам spring значение не подставляет почему-то
            .build();
        urlRepository.save(modelUrl);

        return shorUrl;
    }

    public String getUrl(String shortUrl) {
        //urlCacheRepository.findUrl(shortUrl);
        Url modelUrl = urlRepository.findByShortUrl(shortUrl).orElseThrow(() -> {
            String message = "short link not found for url: %s".formatted(shortUrl);
            return new RuntimeException(message);
        });
        return modelUrl.getUrl();
    }
}
