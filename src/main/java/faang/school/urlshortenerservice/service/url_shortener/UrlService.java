package faang.school.urlshortenerservice.service.url_shortener;

import faang.school.urlshortenerservice.config.async.ThreadPool;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.properties.HashCacheQueueProperties;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import faang.school.urlshortenerservice.service.hash_cache.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCacheQueueProperties queueProp;
    private final UrlRepositoryImpl urlRepository;
    private final RedisCacheManager cacheManager;
    private final ThreadPool threadPool;
    private final HashCache hashCache;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @Value("${url.domain}")
    private String domain;

    @Transactional
    public String shortenUrl(UrlDto urlDto) {
        String hash = getHash();
        urlRepository.save(hash, urlDto.getUrl());
        log.info("Hash - {}, Url - {} was saved to Url repository", hash, urlDto.getUrl());

        Objects.requireNonNull(cacheManager.getCache("hash")).put(hash, urlDto.getUrl());
        log.info("Hash - {}, Url - {} was saved to UrlCache repository", hash, urlDto.getUrl());

        String shortenedUrl = domain + hash;
        log.info("Shortened Url - {}, was created", shortenedUrl);
        return shortenedUrl;
    }

    @Transactional
    @Cacheable(value = "hash", key = "#hash")
    public String getOriginalUrl(String hash) {
        log.info("Hash - {} was missing from Cache", hash);

        return urlRepository.findOriginalUrlByHash(hash).
                orElseThrow(() -> new EntityNotFoundException("No url found for hash: " + hash));
    }

    private String getHash() {
        if (isNecessaryToFill()) {
            if (!isFilling.get()) {
                isFilling.compareAndSet(false, true);

                CompletableFuture.runAsync(() -> hashCache.fillCache().thenRun(() -> {
                    isFilling.compareAndSet(true, false);
                    log.info("Finished filling local cache");
                }), threadPool.hashCacheFillExecutor());
            }
        }
        String hash = hashCache.getLocalHashCache().poll();
        log.info("Hash {} was got from local cache", hash);
        return hash;
    }

    private boolean isNecessaryToFill() {
        return hashCache.getLocalHashCache().size() < getPercentageToStartFill();
    }

    private double getPercentageToStartFill() {
        return (((double) queueProp.getMaxQueueSize()) / 100.00) * queueProp.getPercentageToStartFill();
    }
}
