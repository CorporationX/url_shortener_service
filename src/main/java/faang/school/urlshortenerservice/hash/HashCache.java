package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${executor.queueCapacity}")
    private int cacheSize;

    @Value("${hashCache.percent-minimal-value}")
    private double percentToMinValueQueue;

    private final ExecutorService executorService;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private BlockingQueue<String> hashQueue;

    @PostConstruct
    private void init() throws ExecutionException, InterruptedException {
        this.hashQueue = new LinkedBlockingQueue<>(cacheSize);
        List<String> hashes = hashRepository.getHashBatch(cacheSize);
        hashes.forEach(hashQueue::offer);
        hashGenerator.generateBatch();
    }

    public String getHash() {
        double minQueueSize = cacheSize * percentToMinValueQueue;
        if (hashQueue.size() > minQueueSize) {
            return hashQueue.poll();
        } else {
            if (isRefilling.compareAndSet(false, true)) {
                executorService.execute(() -> {
                    int batchSize = cacheSize - hashQueue.size();
                    List<String> hashes = hashRepository.getHashBatch(batchSize);
                    hashes.forEach(hashQueue::offer);
                    try {
                        hashGenerator.generateBatch();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    isRefilling.set(false);
                });
            }
        }
        return hashQueue.poll();
    }
}

