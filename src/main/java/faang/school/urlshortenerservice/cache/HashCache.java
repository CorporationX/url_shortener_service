package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.executor.ExecutorService;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.HashRepositoryImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashCache {
    private final int threshold;

    private final ArrayBlockingQueue<String> cache;
    private final ExecutorService executorService;
    private final AtomicBoolean isFilling =  new AtomicBoolean(false);

    public HashCache(ExecutorService executorService,
                     @Value("${hash.cache.load-factor}") double loadFactor,
                     @Value("${hash.cache.capacity}") int capacity) {
        this.executorService = executorService;
        cache = new ArrayBlockingQueue<>(capacity);
        threshold = (int) Math.ceil(loadFactor * capacity);
        fillCache();
    }

    public String getHash() {
        if (cache.size() <= threshold) {
            fillCache();
        }
        return cache.poll();
    }

    private void fillCache() {
        if (isFilling.compareAndSet(false, true)) {
            executorService.fillCache()
                    .thenAccept(cache::addAll)
                    .thenRun(() -> isFilling.set(false));
        }
    }
}
