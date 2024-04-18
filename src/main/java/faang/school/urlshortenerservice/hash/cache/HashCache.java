package faang.school.urlshortenerservice.hash.cache;

import faang.school.urlshortenerservice.hash.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    @Value("${local-cache.minimalAmountInPercentage}")
    private int minimalAmountInPercentage;
    @Value("${local-cache.maxSize}")
    private int maxSize;
    @Value("${local-cache.maxWaitTimeMillis}")
    private int maxWaitTime;
    private final int minimalAmount = maxSize * minimalAmountInPercentage / 100;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final AtomicBoolean generateHashesRunning = new AtomicBoolean(false);

    @PostConstruct
    public void initCache() {
        addAndGenerateHashesIfNecessary();
    }

    public String getNextUniqueHash() {
        addAndGenerateHashesIfNecessary();
        String nextHash = cache.poll();
        if (nextHash == null) {
            try {
                synchronized (cache) {
                    cache.wait(maxWaitTime);
                }
                String hash = cache.poll();
                if (hash != null) {
                    return hash;
                }
                throw new RuntimeException("Unable to get next hash after waiting");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted", e);
            }
        }
        return nextHash;
    }

    private void addAndGenerateHashesIfNecessary() {
        if (generateHashesRunning.compareAndSet(false, true)) {
            taskExecutor.submit(() -> {
                if (cache.isEmpty() || cache.size() <= minimalAmount) {
                    cache.addAll(hashRepository.getHashBatch());
                    synchronized (cache) {
                        cache.notifyAll();
                    }
                    hashGenerator.generateHash();
                    generateHashesRunning.set(false);
                }
            });
        }
    }

}
