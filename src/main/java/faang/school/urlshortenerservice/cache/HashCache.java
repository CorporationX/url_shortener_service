package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private Queue<String> hashQueue;
    @Value("${cache.collection-capacity}")
    private int collectionCapacity;
    @Value("${cache.percentage-of-initial-size-fill}")
    private int percentageOfInitialSizeFill;

    @PostConstruct
    public void setUp() {
        hashQueue = new ArrayBlockingQueue<String>(collectionCapacity);
        hashQueue.addAll(hashGenerator.generateBatch(collectionCapacity));
    }

    public String getHash() {
        return hashQueue.poll();
    }

    public void fillingCache(List<String> hashes) {
        hashQueue.addAll(hashes);
    }

    public boolean cacheSizeLessThanRequired() {
        if (100 * (hashQueue.size() / collectionCapacity) <= percentageOfInitialSizeFill) {
            return true;
        }
        return false;
    }

    public int getFreeCapacityInCollection() {
        return collectionCapacity - hashQueue.size();
    }
}
