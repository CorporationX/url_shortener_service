package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private AtomicBoolean isFilling = new AtomicBoolean(false);
    private Queue<String> queue;

    @Value("${spring.task.array_blocking_queue_capacity}")
    private int capacity;
    @Value("${spring.task.fill_percent}")
    private double fillPercent;

    @PostConstruct
    public void init() {
        queue = new ArrayBlockingQueue<>(capacity);
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
