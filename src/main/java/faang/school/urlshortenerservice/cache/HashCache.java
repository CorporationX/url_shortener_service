package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
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
        hashQueue = new LinkedBlockingDeque<>(cacheSize);
        hashQueue.addAll(hashGenerator.getHashes());
        log.info("Кэш хэшей инициализирован");
    }

    public String getHash() {
        if (hashQueue.size() < cacheSize * minFillPercent) {
            if (isGenerating.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync()
                        .thenAccept(hashQueue::addAll)
                        .thenRun(() -> isGenerating.set(false));
                log.info("Получены новые хэши");
            }
        }

        try {
            String hash = hashQueue.poll();
            if (hash == null) {
                log.error("Свободный хэш отсутствует");
                throw new HashCacheException("Свободный хэш отсутствует");
            }

            return hash;
        } catch (Exception e) {
            log.error("Операция прервана", e);
            Thread.currentThread().interrupt();
            throw new HashCacheException("Операция прервана");
        }
    }
}
