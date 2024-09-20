package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    @Value("${hash.queue_capacity}")
    private int capacity;
    @Value("${hash.min_fill_percent}")
    private int minFillPercent;
    private BlockingQueue<Hash> queue;

    @PostConstruct
    public void init() {
        queue = new LinkedBlockingDeque<>(capacity) ;
        fillingQueue();
    }

    public String getHash() {
        if (queue.size() / (capacity / 100.0) < minFillPercent) {
            if (isGenerating.compareAndSet(false, true)) {
                hashGenerator.getBatch();
                hashGenerator.getBatchAsync().thenAccept(queue::addAll);
                isGenerating.set(false);
            }
        }
        return queue.poll().getHash();
    }

    public void fillingQueue() {
        hashGenerator.generatedBatch();
        queue.addAll(hashGenerator.getBatch());
    }
}