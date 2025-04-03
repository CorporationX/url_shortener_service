package faang.school.urlshortenerservice.Cache;

import faang.school.urlshortenerservice.HashGenerator.HashGenerator;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final AtomicBoolean isLocked = new AtomicBoolean(false);
    @Qualifier("hashGeneratorThreadPool")
    private final ThreadPoolTaskExecutor executorService;
    @Value("${hash.cache.size}")
    private int cacheSize;
    @Value("${hash.cache.threshold}")
    private double threshold;
    private Queue<String> hashQueue;

    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(cacheSize);
    }

    public String getHash() {
        if (hashQueue.size() < cacheSize * threshold
                && isLocked.compareAndSet(false, true)) {
            CompletableFuture.runAsync(() -> {
                try {
                    hashGenerator.generateHash();
                    hashRepository.getHashBatch(cacheSize - hashQueue.size()).stream()
                            .map(Hash::getHash)
                            .forEach(hashQueue::add);
                } finally {
                    isLocked.set(false);
                }
            }, executorService);
        }
        String result = hashQueue.poll();
        if (hashQueue.isEmpty() || result == null) {
            throw new NoSuchElementException("Отсутствуют свободные хэши");
        }
        return result;
    }
}
