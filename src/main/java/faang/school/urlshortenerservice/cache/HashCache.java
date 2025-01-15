package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final BlockingQueue<String> cache = new LinkedBlockingQueue<>();

    private final HashGenerator hashGenerator;

    private static final int CACHE_THRESHOLD = 100;
    private static final int CACHE_SIZE = 1000;

    public String getHash() {
        refillCacheIfNeeded();
        try {
            return cache.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Ошибка при получении хэша из кэша", e);
            throw new RuntimeException("Не удалось получить хэш из кэша");
        }
    }

    private synchronized void refillCacheIfNeeded() {
        if (cache.size() < CACHE_THRESHOLD) {
            log.info("Пополнение кэша хэшей...");
            int batchSize = CACHE_SIZE - cache.size();
            var newHashes = hashGenerator.generateBatch(batchSize);
            cache.addAll(newHashes);
            log.info("Кэш хэшей пополнен, размер: {}", cache.size());
        }
    }
}


