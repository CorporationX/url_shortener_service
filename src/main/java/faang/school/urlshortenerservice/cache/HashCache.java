package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final AtomicBoolean indicateGenerationHash = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;
    private Queue<String> hashQueue;
    @Value("${cache.collection-capacity}")
    private int collectionCapacity;
    @Value("${cache.percentage-of-initial-size-fill}")
    private int percentageOfInitialSizeFill;

    @PostConstruct
    public void setUp() {
        hashQueue = new ArrayBlockingQueue<String>(collectionCapacity);
        hashQueue.addAll(hashGenerator.generateBatch());
    }

    public String getHash() {
        if (cacheSizeLessThanRequired()) {

        }
        return hashQueue.remove();
    }

    public boolean cacheSizeLessThanRequired() {
        if (100 * (hashQueue.size() / collectionCapacity) <= percentageOfInitialSizeFill) {
            return true;
        }
        return false;
    }

    public void fillingCacheWithHashes(List<String> hashes) {
        hashQueue.addAll(hashes);
    }

    public boolean getIndicateGenerationHash() {
        return indicateGenerationHash.get();
    }

    public void setIndicateGenerationHashInTrue() {
        indicateGenerationHash.set(true);
    }

    public void setIndicateGenerationHashInFalse() {
        indicateGenerationHash.set(false);
    }
}
