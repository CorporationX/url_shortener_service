package faang.school.urlshortenerservice.generate;

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

    @Value("${hash_cache_size}")
    private int hashCacheSize;
    @Value("${fill_percent}")
    private int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);
    private Queue<String> hashCache;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        hashCache = new ArrayBlockingQueue<>(hashCacheSize);
        hashCache.addAll(hashGenerator.getHashes(hashCacheSize));
    }

    public String getHash() {
        if (calculateFillPercentage()) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(hashCacheSize)
                        .thenAccept(hashCache::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashCache.poll();
    }

    private boolean calculateFillPercentage() {
        return hashCache.size() / hashCacheSize * 100 < fillPercent;
    }

}
