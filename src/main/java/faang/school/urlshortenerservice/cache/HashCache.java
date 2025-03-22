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

    @Value("${hash.cache.max-size}")
    private int maxSize;

    @Value("${hash.cache.threshold}")
    private int thresholdPercentage;

    private ArrayBlockingQueue<String> hashQueue;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    public HashCache(HashRepository hashRepository,
                     HashGenerator hashGenerator,
                     @Qualifier("hashCacheTaskExecutor") ExecutorService taskExecutor) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(maxSize);
        refillCache();
    }

    public String getHash() {
        String hash = hashQueue.poll();
        if (hash == null) {
            refillCache();
            hash = hashQueue.poll();
        } else {
            checkAndRefill();
        }
        if (hash == null) {
            throw new IllegalStateException("No hashes available in cache");
        }
        return hash;
    }

    private void checkAndRefill() {
        double currentPercentage = (double) hashQueue.size() / maxSize * 100;
        if (currentPercentage < thresholdPercentage) {
            refillCache();
        }
    }

    private void refillCache() {
        if (isRefilling.compareAndSet(false, true)) {
            taskExecutor.execute(() -> {
                try {
                    hashGenerator.generateBatch();
                    List<String> hashes = hashRepository.getHashBatch();
                    hashQueue.addAll(hashes);
                } finally {
                    isRefilling.set(false);
                }
            });
        }
    }
}