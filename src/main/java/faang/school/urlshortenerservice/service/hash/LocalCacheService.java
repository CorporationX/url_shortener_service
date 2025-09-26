package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalCacheService {

    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity:10}")
    private int capacity;

    @Value("${hash.cache.low-threshold-percent:20}")
    private int lowPercent;

    @Value("${hash.cache.max-await-on-empty-ms:2000}")
    private int maxWaitForValueMS;

    private final AtomicBoolean isRefilling = new AtomicBoolean();

    private ArrayBlockingQueue<String> localCache;

    @PostConstruct
    public void init() {
        localCache = new ArrayBlockingQueue<>(capacity);
        seedCache();
    }

    public String getHash() {
        maybeRefill();
        try {
            String hash = localCache.poll(maxWaitForValueMS, TimeUnit.MILLISECONDS);
            if (hash == null) {
                throw new IllegalStateException("Hash cache empty after timeout");
            }
            return hash;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for hash", e);
        }
    }

    protected void maybeRefill() {
        double fillPercent = (localCache.size() * 100.0) / capacity;
        if (fillPercent <= lowPercent) {
            if (isRefilling.compareAndSet(false, true)) {
                hashGenerator.getHashes(capacity - localCache.size())
                        .thenAccept(localCache::addAll)
                        .whenComplete((result, e) -> {
                            if (e != null) {
                                log.error("Hash cache refill failed: {}", e.getMessage(), e);
                            }
                            isRefilling.set(false);
                        });
            }
        }
    }

    private void seedCache() {
        log.info("Seeding cache at startup");
        localCache.addAll(hashGenerator.getHashes(capacity).join());
        log.info("Cache seeding completed");
    }
}
