package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash.cache-size}")
    private int hashCacheSize;

    @Value("${hash.min-cache-percentage}")
    private double minCachePercentage;

    private Queue<String> hashQueue;

    private final HashRepository hashRepository;

    private final HashGenerator hashGenerator;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    @PostConstruct
    public void warmUpCache() {
        hashQueue = new ArrayBlockingQueue<>(hashCacheSize);
        hashGenerator.generateBatch();
        List<String> hashes = hashRepository.getHashBatch(hashCacheSize);
        hashQueue.addAll(hashes);
        log.info("Кэш хэшей инициализирован");
    }

    public String getHash() {
        log.info("Текущее количество хэшей в кеше: {}", hashQueue.size());
        String hash = hashQueue.poll();
        if (hash != null) {
            return hash;
        }

        if (hashQueue.size() < hashCacheSize * minCachePercentage) {
            if (isGenerating.compareAndSet(false, true)) {
                CompletableFuture.runAsync(() -> {
                            hashGenerator.generateBatch();
                            List<String> newHashes = hashRepository.getHashBatch(hashCacheSize - hashQueue.size());
                            hashQueue.addAll(newHashes);
                        }, executorService)
                        .thenRun(() -> isGenerating.set(false));
            }
        }
        return null;
    }
}
