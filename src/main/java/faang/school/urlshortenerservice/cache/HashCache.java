package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.CacheRefillException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static faang.school.urlshortenerservice.exception.ErrorMessages.CACHE_REFILL_FAILED;
import static faang.school.urlshortenerservice.exception.ErrorMessages.HASH_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final BlockingQueue<String> cache = new LinkedBlockingQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @Value("${shortener.cache.max-size}")
    private int maxSize;

    @Value("${shortener.cache.refill-threshold}")
    private double refillThreshold;

    @Qualifier("hashCacheExecutor")
    private final ExecutorService executorService;

    public String getHash() {
        String hash = cache.poll();
        if (hash == null) {
            log.warn(HASH_NOT_FOUND);
            throw new CacheRefillException(HASH_NOT_FOUND);
        }
        log.debug("Hash retrieved from cache: {}", hash);
        tryRefillCache();
        return hash;
    }

    private void tryRefillCache() {
        int currentSize = cache.size();
        int threshold = (int) (maxSize * refillThreshold / 100);

        if (currentSize > threshold) {
            return;
        }

        if (isRefilling.compareAndSet(false, true)) {
            log.info("Starting cache refill");
            executorService.submit(() -> {
                try {
                    hashGenerator.generateBatch();
                    List<String> newHashes = hashRepository.getHashBatch();

                    newHashes.forEach(hash -> {
                        if (cache.size() < maxSize) {
                            cache.offer(hash);
                        }
                    });
                } catch (Exception e) {
                    log.error(CACHE_REFILL_FAILED, e);
                    throw new CacheRefillException(CACHE_REFILL_FAILED);
                } finally {
                    isRefilling.set(false);
                    log.info("Cache refill finished");
                }
            });
        }
    }
}
