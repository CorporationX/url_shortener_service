package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final Executor hashCacheThreadPool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.local-cache.cache-size}")
    private int cacheSize;

    @Value("${hash.local-cache.max-fill-threshold}")
    private double thresholdPercent;

    private BlockingQueue<String> hashQueue;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashQueue = new LinkedBlockingQueue<>(cacheSize);
        hashGenerator.generateBatch();
        List<String> hashes = hashRepository.getHashBatch(cacheSize);
        hashQueue.addAll(hashes);
        log.info("Очередь для хэшей инициализирована, загружено элементов: {}", hashQueue.size());
    }

    public String getHash() {
        if (shouldRefill()) {
            refillCache();
        }
        return hashQueue.poll();
    }

    private boolean shouldRefill() {
        int currentSize = hashQueue.size();
        double threshold = cacheSize * thresholdPercent;
        return currentSize <= threshold && isRefilling.compareAndSet(false, true);
    }

    private void refillCache() {
        hashCacheThreadPool.execute(() -> {
            try {
                List<String> hashes = hashRepository.getHashBatch(cacheSize - hashQueue.size());
                hashQueue.addAll(hashes);
                log.info("Добавлено {} хэшей в кэш.", hashes.size());
                hashGenerator.generateBatch();

            } catch (RuntimeException e) {
                log.error("Ошибка при пополнении кэша", e);
            } finally {
                isRefilling.set(false);
            }
        });
    }
}