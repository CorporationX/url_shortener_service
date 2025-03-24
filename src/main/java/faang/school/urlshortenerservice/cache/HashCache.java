package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);
    private Queue<String> hashQueue;

    @Value("${hash.cache.min-fill-percent:0.2}")
    private double minFillPercent;

    @Value("${hash.cache.size:10000}")
    private int cacheSize;

    @PostConstruct
    public void initCash() {
        hashQueue = new ArrayBlockingQueue<>(cacheSize);
        hashGenerator.generateHash();
        hashQueue.addAll(hashGenerator.getHashes());
        log.info("Кэш хэшей инициализирован");
    }

    public String getHash() {
        verifySizeCache();

        if (hashQueue == null || hashQueue.isEmpty()) {
            log.error("Свободный хэш отсутствует");
            throw new HashCacheException("Свободный хэш отсутствует");
        }

        try {
            return hashQueue.poll();
        } catch (Exception e) {
            log.error("Операция прервана", e);
            Thread.currentThread().interrupt();
            throw new HashCacheException("Операция прервана");
        }
    }

    private void verifySizeCache() {
        if (hashQueue.size() < cacheSize * minFillPercent
                && isGenerating.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync()
                    .thenAccept(hashQueue::addAll)
                    .thenRun(() -> {
                        isGenerating.set(false);
                        log.info("Получены новые хэши");
                    });
        }
    }
}
