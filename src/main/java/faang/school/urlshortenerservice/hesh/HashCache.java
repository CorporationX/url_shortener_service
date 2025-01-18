package faang.school.urlshortenerservice.hesh;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final ExecutorService executorService;

    @Value("${hash.queue.size}")
    private int maxCashSize;
    @Value("${hash.queue.percentage-multiplier}")
    private double lowThresholdPercentage;

    private BlockingDeque<String> cache;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void initializeCache() {
        this.cache = new LinkedBlockingDeque<>(maxCashSize);
    }

    public String getHash() {
        if (cache.size() <= maxCashSize * (lowThresholdPercentage / 100.0)) {
            fillCacheAsync();

        }
        return cache.poll();
    }

    private void fillCacheAsync() {
        if (isFilling.compareAndSet(false, true)) {
            executorService.submit(() -> {
                try {
                    List<String> hashes = hashRepository.getHashBatch();
                    for (String hash : hashes) {
                        cache.offer(hash);
                    }
                } finally {
                    isFilling.set(false);
                }
            });
        }
    }
}
