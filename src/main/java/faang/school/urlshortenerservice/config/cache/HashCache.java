package faang.school.urlshortenerservice.config.cache;

import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private static final double ONE_HUNDRED = 100.0;

    private final CacheProperties cacheProperties;
    private final HashService hashService;

    private final AtomicBoolean filling = new AtomicBoolean(false);
    private Queue<String> hashes;

    @PostConstruct
    void init() {
        hashes = new ArrayBlockingQueue<>(cacheProperties.getCapacity());
        hashes.addAll(hashService.getHashes());
    }

    public String getHash() {
        double currentCapacity = hashes.size() / (cacheProperties.getCapacity() / ONE_HUNDRED);

        if (currentCapacity <= cacheProperties.getFillWhenLess()
                && filling.compareAndSet(false, true)) {
            hashService.getHashesAsync()
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> filling.set(false));
        }

        return hashes.poll();
    }
}
