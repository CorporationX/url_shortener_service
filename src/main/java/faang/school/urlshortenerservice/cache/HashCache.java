package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService taskExecutor;
    private final HashCacheConfig config;

    @Value("${hash.batch.size:10}")
    private int batchSize;

    private ArrayBlockingQueue<String> hashQueue;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    public HashCache(HashRepository hashRepository,
                     HashGenerator hashGenerator,
                     @Qualifier("hashCacheTaskExecutor") ExecutorService taskExecutor,
                     HashCacheConfig config) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.taskExecutor = taskExecutor;
        this.config = config;
    }

    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(config.getMaxSize());
        refillCache();
    }

    public String getHash() {
        checkAndRefillIfNeeded();
        String hash = hashQueue.poll();
        if (hash == null) {
            throw new IllegalStateException("No hashes available in cache");
        }
        return hash;
    }

    private void checkAndRefillIfNeeded() {
        if (hashQueue.isEmpty() || (double) hashQueue.size() / config.getMaxSize() * 100 < config.getThresholdPercentage()) {
            refillCache();
        }
    }

    private void refillCache() {
        if (isRefilling.compareAndSet(false, true)) {
            taskExecutor.execute(() -> {
                try {
                    hashGenerator.generateBatch();
                    List<String> hashes = hashRepository.getHashBatch(batchSize);
                    hashQueue.addAll(hashes);
                } finally {
                    isRefilling.set(false);
                }
            });
        }
    }
}