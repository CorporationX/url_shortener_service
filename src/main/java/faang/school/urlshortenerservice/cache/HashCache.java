package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final Executor urlShortenerExecutor;
    private final StringRedisTemplate redisTemplate;

    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    private final int cacheSize;
    private final double refillPercent;
    private final String hashCacheKey;

    public HashCache(UrlShortenerConfig urlShortenerConfig,
                     HashRepository hashRepository,
                     HashGenerator hashGenerator,
                     @Qualifier("urlShortenerExecutor") Executor urlShortenerExecutor,
                     StringRedisTemplate redisTemplate) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.urlShortenerExecutor = urlShortenerExecutor;
        this.redisTemplate = redisTemplate;

        this.cacheSize = urlShortenerConfig.getCacheSize();
        this.refillPercent = urlShortenerConfig.getRefillPercent();
        this.hashCacheKey = urlShortenerConfig.getHashCacheKey();
    }

    public Optional<String> getHash() {
        long redisCacheSize = redisTemplate.opsForList().size(hashCacheKey);
        long threshold = (long) (cacheSize * refillPercent);
        if (!refillInProgress.get() && redisCacheSize < threshold) {
            refillCache();
        }
        String hash = redisTemplate.opsForList().leftPop(hashCacheKey);
        return Optional.ofNullable(hash);
    }

    private void refillCache() {
        if (!refillInProgress.compareAndSet(false, true)) {
            return;
        }
        urlShortenerExecutor.execute(() -> {
            try {
                log.info("Redis cache refill started");
                List<String> newHashes = hashRepository.getHashBatch();
                if (newHashes != null && !newHashes.isEmpty()) {
                    redisTemplate.opsForList().rightPushAll(hashCacheKey, newHashes);
                }
                try {
                    hashGenerator.generateBatch();
                } catch (Exception exception) {
                    log.error("Error while generate new hash batch", exception);
                }
            } catch (Exception exception) {
                log.error("Error while refill cache", exception);
            } finally {
                refillInProgress.set(false);
                log.info("Refill cache finished");
            }
        });
    }
}
