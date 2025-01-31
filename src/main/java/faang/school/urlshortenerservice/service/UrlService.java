package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${shortener.base_url}")
    private String baseUrl;

    public String createShortUrl(String longUrl) {
        Url existingUrl = urlRepository.findByUrl(longUrl).orElse(null);

        if (existingUrl != null) {
            return baseUrl + existingUrl.getHash();
        }

        String hash = hashCache.getHash();

        Url url = new Url(hash, longUrl, LocalDateTime.now());
        urlRepository.save(url);

        urlCacheRepository.save(hash, longUrl);

        return baseUrl + hash;
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

    @Transactional
    public void cleanupOldUrl(LocalDateTime time) {
        List<String> hashes = urlRepository.deleteOldUrls(time);
        hashRepository.saveBatch(hashes);
    }
}
