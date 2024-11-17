package faang.school.urlshortenerservice.service.hashCache;

import faang.school.urlshortenerservice.config.сache.CacheProperties;
import faang.school.urlshortenerservice.service.hashGenerator.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final CacheProperties cacheProperties;
    private final int capacity;
    private final ArrayBlockingQueue<String> hashQueue;
    private final AtomicBoolean filling;

    @Autowired
    public HashCache(CacheProperties cacheProperties,
                     HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
        this.cacheProperties = cacheProperties;
        this.capacity = cacheProperties.getCapacity();
        this.hashQueue = new ArrayBlockingQueue<>(cacheProperties.getCapacity());
        this.filling = new AtomicBoolean(false);
        hashQueue.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        boolean exceededLimit = hashQueue.size() / (capacity / 100.0) < cacheProperties.getMinLimitCapacity();

        if (exceededLimit) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity - hashQueue.size()).
                        thenAccept(hashQueue::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashQueue.poll();
    }
}
