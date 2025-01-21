package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    @Value("${hash.cache.capacity}")
    private final int capacity;
    @Value("${hash.cache.limit}")
    private final double cacheMinimumLimit;

    private final AtomicBoolean fillingCache = new AtomicBoolean(false);
    private final ArrayBlockingQueue<String> cache = new ArrayBlockingQueue<>(capacity);

    @PostConstruct
    public void initCash(List<String> hashes) {
        cache.addAll(hashGenerator.getHashes());
    }

    public String getHashFromCache() {
        if ((double) capacity / cache.size() < cacheMinimumLimit) {
            if (fillingCache.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync()
                        .thenAccept(cache::addAll)
                        .thenRun(() -> fillingCache.set(false));
            }
        }
        return cache.poll();
    }
}
