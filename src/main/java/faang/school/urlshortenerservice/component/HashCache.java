package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.exceptions.HashCacheInitializationException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class HashCache {

    private final HashGenerator hashGenerator;

    @Value("${hashCache.queue-capacity}")
    private int capacity;

    @Value("${hashCache.fill-percent}")
    private int fillPercent;

    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private Queue<String> hashes;


    @PostConstruct
    public void init() {
        this.hashes = new ArrayBlockingQueue<>(capacity);
        try {
            hashes.addAll(hashGenerator.getHashes((long) capacity));
            log.info("Initialized HashCache with {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Failed to initialize HashCache", e);
            throw new HashCacheInitializationException("HashCache initialization failed", e);
        }
    }

    public String getHash() {
        if (shouldRefill()) {
            refillAsync();
        }
        return hashes.poll();
    }

    private void refillAsync() {
        if (isRefilling.compareAndSet(false, true)) {
            log.debug("Начало асинхронного пополнения кэша");
            int neededHashes = capacity - hashes.size();

            hashGenerator.getHashesAsync((long) neededHashes)
                    .thenAccept(newHashes -> {
                        // Безопасное добавление с проверкой свободного места
                        newHashes.forEach(hash -> {
                            if (!hashes.offer(hash)) {
                                log.warn("Не удалось добавить хеш - кэш переполнен");
                            }
                        });
                        log.debug("Добавлено {} новых хешей", newHashes.size());
                    })
                    .exceptionally(ex -> {
                        log.error("Ошибка при пополнении кэша", ex);
                        return null;
                    })
                    .thenRun(() -> {
                        isRefilling.set(false);
                        log.debug("Пополнение кэша завершено");
                    });
        }
    }

    private boolean shouldRefill() {
        if (hashes.isEmpty()) {
            return true;
        }
        double currentFillPercent = (hashes.size() * 100.0) / capacity;
        return currentFillPercent <= fillPercent;
    }
}
