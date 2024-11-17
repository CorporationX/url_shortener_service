package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocalCache {

    private final HashGenerator hashGenerator;
    private ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private AtomicBoolean isLoading = new AtomicBoolean(false);

    @Value("${generator.hashes-batch-size:100}")
    private int batchSize;

    @Value("${generator.min-cache-fill-ratio:0.2}")
    private float minCacheFillRatio;

    public String getHash() {
        if (cache.size() < batchSize * minCacheFillRatio
                && isLoading.compareAndSet(false, true)) {
            hashGenerator.getBatch(batchSize)
                    .thenAccept(cache::addAll)
                    .thenRun(hashGenerator::generateBatch)
                    .thenRun(() -> isLoading.set(false));
        }
        return cache.poll();
    }
}
