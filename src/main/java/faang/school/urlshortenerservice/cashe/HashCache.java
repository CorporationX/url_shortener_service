package faang.school.urlshortenerservice.cashe;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${custom.hash-cache.size}")
    private int cacheSize;

    @Value("${custom.hash-cache.refill-threshold}")
    private double refillThreshold;

    @Value("${custom.hash-cache.batch-size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;

    private BlockingQueue<String> hashQueue;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        this.hashQueue = new LinkedBlockingQueue<>(cacheSize);
        refillCache();
    }

    public String getHash() {
        String hash = hashQueue.poll();
        if (hash != null) {
            checkAndRefillIfNeeded();
        }
        return hash;
    }

    private void checkAndRefillIfNeeded() {
        if (hashQueue.size() < cacheSize * refillThreshold && isRefilling
                .compareAndSet(false, true)) {
            executorService.submit(this::refillCache);
        }
    }

    private void refillCache() {
        try {
            int requiredHashes = cacheSize - hashQueue.size();
            List<String> newHashes = hashRepository.selectRandomHashes(requiredHashes);
            hashRepository.deleteHashes(newHashes);
            hashQueue.addAll(newHashes);
            hashGenerator.generateBatch();
        } finally {
            isRefilling.set(false);
        }
    }
}

