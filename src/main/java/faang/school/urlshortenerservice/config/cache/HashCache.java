package faang.school.urlshortenerservice.config.cache;

import faang.school.urlshortenerservice.service.hash.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Component
public class HashCache {
    @Value("${app.hash-queue.capacity}")
    private int capacity;

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);
    private final ExecutorService executorService;
    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);

    @PostConstruct
    public void init() {
        hashGenerator.generateBatch();
        List<String> hashesFromDb = hashGenerator.getHashes(1);
        hashes.addAll(hashesFromDb);
    }

    public String getHash() {
        if (hashes.size() / (capacity / 100.0) < 20) {
            if (isGenerating.compareAndSet(false, true)) {
                executorService.submit(() -> {
                    try {
                        List<String> hashesFromDb = hashGenerator.getHashes(1);
                        hashes.addAll(hashesFromDb);
                        hashGenerator.generateBatchAsync();
                    } finally {
                        isGenerating.set(false);
                    }
                });
            }
        }

        return hashes.poll();
    }
}
