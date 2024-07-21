package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.service.generator.HashGenerator;
import faang.school.urlshortenerservice.service.generator.async.AsyncHashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class HashCacheImpl implements HashCache {

    private final int capacity;
    private final double minCapacityPercents;
    private final AsyncHashGenerator asyncHashGenerator;
    private final HashGenerator hashGenerator;
    private final BlockingQueue<String> queue;
    private final Lock fillQueueLock = new ReentrantLock();

    public HashCacheImpl(@Value("${services.hash.cache.capacity}") int capacity,
                         @Value("${services.hash.cache.min-capacity-percents}") double minCapacityPercents,
                         HashGenerator hashGenerator,
                         AsyncHashGenerator asyncHashGenerator) {
        this.capacity = capacity;
        this.minCapacityPercents = minCapacityPercents;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.hashGenerator = hashGenerator;
        this.asyncHashGenerator = asyncHashGenerator;
    }

    @PostConstruct
    public void init() {
        List<String> hashes = hashGenerator.getBatch();
        queue.addAll(hashes);
    }

    @Override
    public String pop() {

        if (queue.size() * 100.0 / capacity < minCapacityPercents) {
            if (fillQueueLock.tryLock()) {
                try {
                    asyncHashGenerator.getBatchAsync()
                            .thenAccept(queue::addAll);
                } finally {
                    fillQueueLock.unlock();
                }
            }
        }

        return queue.poll();
    }

    @Override
    public void putAll(List<String> hashes) {
        queue.addAll(hashes);
    }
}
