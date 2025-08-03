package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashCacheProperties properties;
    private final ExecutorService hashCacheExecutor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private BlockingQueue<String> cache;

    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        this.cache = new LinkedBlockingQueue<>(properties.getCapacity());
    }

    public String getHash() {
        triggerRefillIfNeeded();

        String hash = cache.poll();
        if (hash == null) {
            throw new IllegalStateException("Нет доступных хэшей в кэше");
        }

        return hash;
    }

    private void triggerRefillIfNeeded() {
        if (isRefillNeeded() && refillInProgress.compareAndSet(false, true)) {
            log.info("Запуск refill кэша хэшей (current size: {})", cache.size());

            hashCacheExecutor.submit(() -> {
                try {
                    refillCache();
                } catch (Exception e) {
                    log.error("Ошибка при refill кэша хэшей", e);
                } finally {
                    refillInProgress.set(false);
                }
            });

            hashCacheExecutor.submit(() -> {
                try {
                    hashGenerator.generateBatch();
                } catch (Exception e) {
                    log.error("Ошибка при генерации новых хэшей", e);
                }
            });
        }

    }


    private boolean isRefillNeeded() {
        int currentSize = cache.size();
        int maxSize = properties.getCapacity();
        double threshold = properties.getRefillThresholdPercentage();
        return ((double) currentSize / maxSize) < threshold;
    }

    private void refillCache() {
        List<String> freeHashes = hashRepository.getHashBatch();

        for (String hash : freeHashes) {
            if (!cache.offer(hash)) {
                break; // если очередь заполнена
            }
        }

        log.info("Кэш пополнен {} хэшами (текущий размер: {})", freeHashes.size(), cache.size());
    }
}
