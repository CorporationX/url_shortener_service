package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache_size:1000}")
    private int cacheSize;

    private final HashGenerator hashGenerator;
    private BlockingQueue<Hash> hashQueue;

    private final AtomicBoolean isRunNewGenerator = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        this.hashQueue = new ArrayBlockingQueue<>(cacheSize);
        hashQueue.addAll(hashGenerator.generateBatch(cacheSize));
    }

    public Hash getHash() {
        if ((hashQueue.size() * 100) / cacheSize < 20) {
            if (!isRunNewGenerator.compareAndExchange(false, true)) {
                hashGenerator.generateBatchAsync(cacheSize - hashQueue.size())
                        .thenAccept(hashQueue::addAll)
                        .thenRun(()->isRunNewGenerator.set(false));
            }
        }
        return hashQueue.poll();
    }
}
