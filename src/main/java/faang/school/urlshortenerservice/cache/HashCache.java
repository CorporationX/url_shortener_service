package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling;
    private final Queue<String> queue;
    private final int capacity;
    private final double fillPercent;

    public HashCache(HashGenerator hashGenerator,
                     @Value("${spring.task.array_blocking_queue_capacity}") int capacity,
                     @Value("${spring.task.fill_percent}") double fillPercent) {
        this.hashGenerator = hashGenerator;
        this.capacity = capacity;
        this.fillPercent = fillPercent;
        this.isFilling = new AtomicBoolean(false);
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    @PostConstruct
    private void init() {
        queue.addAll(hashGenerator.getHashBatch(capacity));
    }

    public String getHash() {
        int size = queue.size();
        if (size <= capacity * fillPercent) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashBatchAsync(capacity)
                        .thenAccept(queue::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        }
        return queue.poll();
    }
}
