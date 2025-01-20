package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public String createShortUrl(String longUrl) {
        Url existingUrl = urlRepository.findByUrl(longUrl).orElse(null);

        if (existingUrl != null) {
            return "localhost:8080/api/v1/shortener/shortUrl/redirect/" + existingUrl.getHash();
        }

        String hash = hashCache.getHash();

        Url url = new Url(hash, longUrl, LocalDateTime.now());
        urlRepository.save(url);

        urlCacheRepository.save(hash, longUrl);

        return "localhost:8080/api/v1/shortener/shortUrl/redirect/" + hash;
    }

    public String getOriginalUrl(String hash) {
        String longUrl = urlCacheRepository.find(hash);

        if (longUrl != null) {
            log.info("Found URL in Redis for hash: {}", hash);
            return longUrl;
        }

        longUrl = urlRepository.findByHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("URL not found for hash: " + hash));
        log.info("Found URL in database for hash: {}", hash);

        urlCacheRepository.save(hash, longUrl);
        return longUrl;
    }
}
