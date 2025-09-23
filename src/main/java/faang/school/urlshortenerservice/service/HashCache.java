package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * HashCache — осуществляет внутреннее кэширование свободных хэшей,
 * асинхронно заполняет этот кэш без блокирования запросов и является полностью потокобезопасным.
 *
 * @author bozya
 * @since 18.09.2025
 */
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private final BlockingQueue<String> hashes = new LinkedBlockingQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @Value("${app.cache.max-size}")
    private int maxSize;

    @Value("${app.cache.refill-percent}")
    private int refillPercent;

    /**
     * Извлекает хэш из кэша. При необходимости инициирует пополнение кэша.
     *
     * @return хэш или null если кэш пуст
     */
    public String getHash() {
        String hash = hashes.poll();

        if (hash != null && shouldRefill()) {
            triggerRefillOnce();
        }

        return hash;
    }

    /**
     * Проверяет необходимость пополнения кэша на основе текущего размера.
     *
     * @return true если требуется пополнение
     */
    private boolean shouldRefill() {
        return ((double) hashes.size() / maxSize) * 100 < refillPercent;
    }

    /**
     * Инициирует однократное пополнение кэша, если оно еще не выполняется.
     * Гарантирует отсутствие параллельных операций пополнения.
     */
    private void triggerRefillOnce() {
        if (isRefilling.compareAndSet(false, true)) {
            try {
                List<String> newHashes = hashRepository.getHashBatch();
                hashes.addAll(newHashes);

                hashGenerator.generateBatch();
            } finally {
                isRefilling.set(false);
            }
        }
    }

    @PostConstruct
    public void init() {
        if (hashes.isEmpty()) {
            triggerRefillOnce();
        }
    }
}