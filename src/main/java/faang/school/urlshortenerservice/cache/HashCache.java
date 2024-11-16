package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${cache.capacity:100000}")
    private int capacity;

    @Value("${cache.min_percent_hashes:20}")
    private long minPercentHashes;

    private final AtomicBoolean generateIsProcessing = new AtomicBoolean(false);

    private final Queue<String> hashes = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        checkAndGenerateHashes();
        return hashes.poll();
    }

    private void checkAndGenerateHashes() {
        double cacheFullPercentage = 100.0 / capacity * hashes.size();

        if (cacheFullPercentage < minPercentHashes) {
            if (generateIsProcessing.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .handle((result, exception) -> {
                            generateIsProcessing.set(false);
                            if (exception != null) {
                                log.error("Error during hash generation", exception);
                            }
                            return result;
                        });
            }
        }
    }
}
