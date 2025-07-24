package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlHashCache;
import faang.school.urlshortenerservice.entity.UrlHash;
import faang.school.urlshortenerservice.repository.cassandra.UrlHashRepository;
import faang.school.urlshortenerservice.scheduler.HashGeneratorScheduler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlHashRepository urlHashRepository;
    private final UrlHashCache urlHashCache;
    private final HashGeneratorScheduler hashGeneratorScheduler;

    @Override
    public String getFullUrl(String shortUrl) {
        String fullUrl = urlHashCache.get(shortUrl);
        if (fullUrl != null) {
            log.info("Full URL for hash {} retrieved from cache: {}", shortUrl, fullUrl);
            return fullUrl;
        }

        // Если не в кэше, ищем в Cassandra
        UrlHash urlHash = urlHashRepository.findById(shortUrl)
                .orElseThrow(() -> {
                    log.warn("Short URL {} not found in Cassandra.", shortUrl);
                    return new EntityNotFoundException("Short URL not found");
                });

        // Кладем найденный URL в кэш для будущих запросов (с TTL)
        urlHashCache.put(urlHash.getHash(), urlHash.getFullUrl(), 120); // TTL 120 секунд
        log.info("Full URL for hash {} retrieved from Cassandra and cached: {}", shortUrl, urlHash.getFullUrl());
        return urlHash.getFullUrl();
    }

    // Заменить на ДТО что-бы повесить проверку на URL
    @Override
    public String createShortUrl(String fullUrl) {
        String hash = hashCache.getAnyFirstHash();

        if (hashCache.getCapacity() / 10 > hashCache.getCurrentSize()) {
            log.info("{}/{}", hashCache.getCapacity(), hashCache.getCurrentSize());
            hashGeneratorScheduler.generateBatch();
        }

        String shortUrl = shortenFullUrl(hash);

        // Cassandra UPSERT UPdate or inSERT
        urlHashRepository.save(UrlHash.builder()
                .fullUrl(fullUrl)
                .hash(hash)
                .build());
        // UrlHashCache
        urlHashCache.put(hash, fullUrl, 120);

        log.info("In UrlHashCache -> {}", urlHashCache.getSize());

        return shortUrl;
    }

    private String shortenFullUrl(String hash){
        return "mylink.com/" + hash;
    }
}