package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${application.host:localhost}")
    private String host;

    private final UrlRepository repository;
    private final UrlCacheRepository cacheRepository;
    private final HashCache cache;

    public String getOriginalUrl(String shortenedUrl) {
        Optional<String> optionalUrl = cacheRepository.getByHash(shortenedUrl);

        if (optionalUrl.isPresent()) {
            return optionalUrl.get();
        }
        optionalUrl = repository.getByHash(shortenedUrl);

        return optionalUrl
                .orElseThrow(() -> new RuntimeException("Url not found"));

    }

    public String createShortenedUrl(UrlDto url) {
        String hash = cache.getHash();
        String shortenedUrl = "http://" +
                host +
                "/" +
                hash;
        saveUrl(hash, url);

        return shortenedUrl;
    }

    private void saveUrl(String shortenedUrl, UrlDto url) {
        Url urlEntity = Url.builder()
                .hash(shortenedUrl)
                .url(url.getUrl())
                .build();

        repository.save(urlEntity);
    }
}
