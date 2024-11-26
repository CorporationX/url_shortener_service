package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {
 //   @Value("${generator.hash.cache.capacity}")
    private int capacity;
 //   @Value("${generator.hash.cache.minPercent}")
    private double fillPercent;
    private final Queue<String> hashQueue;// = new ArrayBlockingQueue<>(capacity);
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling;// = new AtomicBoolean(false);

    public HashCache(HashGenerator hashGenerator,
                     @Value("${generator.hash.cache.capacity}") int capacity,
                     @Value("${generator.hash.cache.minPercent}") double fillPercent) {
        this.hashGenerator = hashGenerator;
        this.capacity = capacity;
        this.fillPercent = fillPercent;
        this.isFilling = new AtomicBoolean(false);
        this.hashQueue = new ArrayBlockingQueue<>(capacity);
    }



    @PostConstruct()
    public void init() {
        int i = 0;
        List<String> hashes = hashGenerator.getHashBatch(capacity);
        hashQueue.addAll(hashes);
    }

    public String getHash() {
        if (hashQueue.size() < (fillPercent * capacity) / 100) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashBatchAsync(capacity)
                        .thenAccept(hashQueue::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        }
        return hashQueue.poll();
    }
}
