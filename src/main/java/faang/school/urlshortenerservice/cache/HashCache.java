package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Data
public class HashCache {

    @Qualifier("hashCacheThreadPool")
    private final ExecutorService executor;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @Value("${hash-cache.queue-capacity:10000}")
    private int queueCapacity;
    @Value("${hash-cache.queue-percent:20}")
    private int hashQueuePercent;

    private Queue<String> hashQueue;

    @PostConstruct
    void init() {
        hashQueue = new ArrayBlockingQueue<>(queueCapacity);
        hashQueue.addAll(hashRepository.getHashBatch(queueCapacity - hashQueue.size()));

        if (hashQueue.size() < queueCapacity) {
            hashGenerator.generateBatch();
            hashQueue.addAll(hashRepository.getHashBatch(queueCapacity - hashQueue.size()));
        }
    }

    public String getHash() {
        if (hashQueue.size() < (double) hashQueuePercent / 100 * queueCapacity) {
            fillQueueAsync();
        }

        return hashQueue.poll();
    }

    private void fillQueueAsync() {
        if (isFilling.compareAndSet(false, true)) {
            executor.submit(() -> {
                try {
                    List<String> hashes = hashRepository.getHashBatch(queueCapacity - hashQueue.size());
                    hashQueue.addAll(hashes);
                    hashGenerator.generateBatch();
                } finally {
                    isFilling.set(false);
                }
            });
        }
    }
}