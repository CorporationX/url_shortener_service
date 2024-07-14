package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
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
        hashThreadPool.submitCompletable(hashGenerator::generateBatch);
        cacheQueue.addAll(hashRepository.getHashBatch());
        log.info("init method, queue size {}", cacheQueue.size());
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
