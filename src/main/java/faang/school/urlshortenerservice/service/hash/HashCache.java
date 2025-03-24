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
    @Value("${hash.generate.minimal-percent}")
    private double MIN_PERCENT;
    @Value("${hash.cache.size}")
    private int hashCacheSize;

    private final HashCacheAsync hashCacheAsync;
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public String getHash() {
        String hash = queue.poll();
        if (queue.size() < hashCacheSize * MIN_PERCENT) {
            hashCacheAsync.fillCache(queue, isRunning);
        }
        return hash;
    }
}
