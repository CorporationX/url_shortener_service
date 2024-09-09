package faang.school.urlshortenerservice.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Evgenii Malkov
 */
@Component
public class HashCache {

    @Value("${hash.cache.minimumRequiredPercent}")
    private int minimumRequiredPercent;
    private final Queue<String> hashes;
    @Getter
    private final int initialCapacity;

    public HashCache(@Value("${hash.cache.capacity}") int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.hashes = new ArrayBlockingQueue<>(initialCapacity);
    }

    public String getHash() {
        return this.hashes.poll();
    }

    public void fillCache(List<String> hashes) {
        this.hashes.addAll(hashes);
    }

    public boolean isCacheSizeLessMinimumRequired() {
        return (100.0 * this.hashes.size())/(this.initialCapacity) <= minimumRequiredPercent;
    }
}
