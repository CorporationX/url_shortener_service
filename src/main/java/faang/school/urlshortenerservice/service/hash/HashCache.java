package faang.school.urlshortenerservice.service.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.size}")
    private int hashCacheSize;
    private final HashGenerator hashGenerator;
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public String getHash() {
        String hash = queue.poll();
        if (queue.size() < hashCacheSize * 0.2) {
            fillCache();
        }
        return hash;
    }

    @Async("hashCacheExecutor")
    public void fillCache() {
        if (isRunning.compareAndSet(false, true)) {
            try {
                hashGenerator.generateBatch()
                        .thenAccept(queue::addAll)
                        .whenComplete((res, ex) -> {
                            isRunning.set(false);
                        });
            } catch (Exception e) {
                isRunning.set(false);
            }
        }
    }
}
