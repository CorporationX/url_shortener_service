package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlHashCache;
import faang.school.urlshortenerservice.entity.UrlHash;
import faang.school.urlshortenerservice.repository.cassandra.UrlHashRepository;
import faang.school.urlshortenerservice.scheduler.HashGeneratorScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private static final String DOMAIN_URL = "mylink.com/";

    private final HashCache hashCache;
    private final UrlHashRepository urlHashRepository;
    private final UrlHashCache urlHashCache;
    private final HashGeneratorScheduler hashGeneratorScheduler;

    @Override
    public String getFullUrl(String shortUrl) {
        String hash = extractHashFromShortUrl(shortUrl);

        if (hash == null) {
            throw new IllegalArgumentException(
                    String.format("Wasn't able to extract hash from short url: %s", shortUrl));
        }

        String fullUrl = urlHashCache.get(hash);

        log.info("Full URL for hash {} retrieved from Cassandra and cached: {}", shortUrl, fullUrl);
        return fullUrl;
    }

    // Заменить на ДТО что-бы повесить проверку на URL
    @Override
    public String createShortUrl(String fullUrl) {
        String hash = hashCache.getAnyFirstHash();

        if (hashCache.getCapacity() / 10 > hashCache.getCurrentSize()) {
            log.info("{}/{}", hashCache.getCapacity(), hashCache.getCurrentSize());
            hashGeneratorScheduler.triggerGeneration();
        }

        String shortUrl = shortenFullUrl(hash);

        // Cassandra UPSERT UPdate or inSERT
        urlHashRepository.save(UrlHash.builder()
                .fullUrl(fullUrl)
                .hash(hash)
                .build());
        // UrlHashCache
        urlHashCache.put(hash, fullUrl, 120);

        return shortUrl;
    }

    private String shortenFullUrl(String hash) {
        return "mylink.com/" + hash;
    }

    private String extractHashFromShortUrl(String shortUrl) {
        if (shortUrl.startsWith(DOMAIN_URL)) {
            String hash = shortUrl.substring(DOMAIN_URL.length());
            log.info("returning hash: {} from shortUrl: {}", hash, shortUrl);
            return hash;
        }

        return null;
    }
}