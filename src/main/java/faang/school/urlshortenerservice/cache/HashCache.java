package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashGenerationService;
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
    @Value("${cache.parameters.max-capacity}")
    private int maxCapacity;

    @Value("${cache.parameters.lower-bound-percent}")
    private int lowerBoundPercentage;

    private final HashGenerationService hashGenerationService;
    private final AtomicBoolean isLock = new AtomicBoolean(false);
    private Queue<String> hashQueue;

    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(maxCapacity);
        hashQueue.addAll(hashGenerationService.getHashes(maxCapacity));
    }

    private boolean isCacheLessLowerBound() {
        return (100.0 * hashQueue.size())/(maxCapacity) < lowerBoundPercentage;
    }

    public String getHash(){
        if (isCacheLessLowerBound()) {
            if (isLock.compareAndSet(false, true)) {
                hashGenerationService
                        .getHashesAsync(maxCapacity)
                        .thenAccept(hashQueue::addAll)
                        .thenRun(()->isLock.set(false));
            }
        }
        return hashQueue.poll();
    }
}
