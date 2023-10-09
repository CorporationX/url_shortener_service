package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashService hashService;

    @Value("${spring.cache.capacity}")
    private int cacheCapacity;

    @Value("${spring.cache.min-size-percent}")
    private int minSizePercent;
    BlockingQueue<String> hashQueue = new ArrayBlockingQueue<>(cacheCapacity);

    public boolean add(String hash) {
        return hashQueue.add(hash);
    }

    public Optional<String> get() {
        if (hasReachedAllowedPercentCapacity())
            fillCache();
        return Optional.ofNullable(hashQueue.poll());
    }

    private void fillCache() {
        hashService.findAndDelete()
                .thenAccept(hashQueue::addAll);
    }

    private boolean hasReachedAllowedPercentCapacity() {
        return hashQueue.size() <= cacheCapacity * minSizePercent / 100;
    }
}
