package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashCacheExecutor;

    private final ConcurrentLinkedQueue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @Value("${hash.cache.size:10000}")
    private int cacheSize;

    @Value("${hash.cache.refill-threshold-percent:20}")
    private int refillThresholdPercent;

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache with size={}, refillThreshold={}%", cacheSize, refillThresholdPercent);
        refillCache();
    }

    /**
     * Получает случайный хэш из кэша
     * Если кэш заполнен менее чем на refillThresholdPercent%, запускает асинхронное пополнение
     *
     * @return случайный хэш
     */
    public String getHash() {
        String hash = hashQueue.poll();

        if (hash == null) {
            log.warn("Hash cache is empty, waiting for refill");
            refillCacheSync();
            hash = hashQueue.poll();
        }

        int currentSize = hashQueue.size();
        int threshold = (cacheSize * refillThresholdPercent) / 100;

        log.debug("Current cache size: {}, threshold: {}", currentSize, threshold);

        if (currentSize < threshold) {
            log.info("Cache size ({}) below threshold ({}), triggering async refill", currentSize, threshold);
            triggerAsyncRefill();
        }

        return hash;
    }

    /**
     * Запускает асинхронное пополнение кэша (только если не выполняется в данный момент)
     */
    private void triggerAsyncRefill() {
        if (isRefilling.compareAndSet(false, true)) {
            log.info("Starting async cache refill");

            hashCacheExecutor.submit(() -> {
                try {
                    refillCache();
                    CompletableFuture<Integer> future = hashGenerator.generateBatch();
                    if (future != null) {
                        future.exceptionally(ex -> {
                            log.error("Error generating hash batch", ex);
                            return null;
                        });
                    }
                } catch (Exception e) {
                    log.error("Error during async cache refill", e);
                } finally {
                    isRefilling.set(false);
                    log.info("Async cache refill completed");
                }
            });
        } else {
            log.debug("Cache refill already in progress, skipping");
        }
    }

    /**
     * Синхронно пополняет кэш (используется при инициализации или когда кэш пуст)
     */
    private void refillCacheSync() {
        if (isRefilling.compareAndSet(false, true)) {
            try {
                refillCache();
            } finally {
                isRefilling.set(false);
            }
        }
    }

    /**
     * Получает хэши из БД и добавляет их в кэш
     */
    private void refillCache() {
        log.info("Refilling cache from database");

        try {
            List<String> hashes = hashRepository.getHashBatch();

            if (hashes.isEmpty()) {
                log.warn("No hashes available in database, generating new batch");
                CompletableFuture<Integer> future = hashGenerator.generateBatch();
                if (future != null) {
                    future.get();
                }
                hashes = hashRepository.getHashBatch();
            }

            hashQueue.addAll(hashes);
            log.info("Cache refilled with {} hashes, current size: {}", hashes.size(), hashQueue.size());

        } catch (Exception e) {
            log.error("Failed to refill cache from database", e);
            throw new RuntimeException("Failed to refill hash cache", e);
        }
    }

    /**
     * Возвращает текущий размер кэша (для мониторинга)
     *
     * @return количество доступных хэшей в кэше
     */
    public int getCacheSize() {
        return hashQueue.size();
    }
}