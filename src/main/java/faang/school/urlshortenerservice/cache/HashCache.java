package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {

    private final ThreadPoolTaskExecutor hashThreadPool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFillingCache = new AtomicBoolean(false);
    private Queue<String> cacheQueue;
    ;

    @Value("${cache.queue-size}")
    private int capacity;

    @Value("${cache.fill-percent}")
    private int fillPercent;

    @Autowired
    public HashCache(HashRepository hashRepository, HashGenerator hashGenerator,
                     @Qualifier("HashGeneratorThreadPool") ThreadPoolTaskExecutor hashThreadPool) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.hashThreadPool = hashThreadPool;
    }

    @PostConstruct
    public void init() {
        cacheQueue = new LinkedBlockingQueue<>(capacity);
        CompletableFuture<Void> future = hashThreadPool.submitCompletable(hashGenerator::generateBatch);
        future.thenRun(() -> cacheQueue.addAll(hashRepository.getHashBatch()));
    }

    public String getHash() {
        if (cacheQueue.size() / (capacity / 100.0) < fillPercent) {
            if (isFillingCache.compareAndSet(false, true)) {
                cacheQueue.addAll(hashRepository.getHashBatch());
                hashGenerator.generateBatch();
            }
        }

        return cacheQueue.poll();
    }
}
