package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
@Setter
public class HashCache {

    private final HashGenerator hashGenerator;

    @Value("${hash.generator.refill-threshold-percent}")
    private double refillThresholdPercent;

    @Value("${hash.generator.batch-size}")
    private int maxSize;

    private Queue<String> hashes;

    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void initializeCache() {
        hashes = new LinkedBlockingQueue<>(maxSize);
        hashes.addAll(hashGenerator.getHashes(maxSize));
        log.info("HashCache initialized with {} hashes", hashes.size());
    }

    public Optional<String> getHash() {
        String h = hashes.poll();

        if (h == null) {
            List<String> fresh = hashGenerator.getHashes(maxSize);
            hashes.addAll(fresh);
            h = hashes.poll();
        }

        if (hashes.size() <= (int)(maxSize * refillThresholdPercent)) {
            refillCache();
        }

        return Optional.ofNullable(h);
    }

    private void refillCache() {
        if (isRefilling.compareAndSet(false, true)) {
            log.info("Cache refill triggered. Current size: {}. Threshold: {}.",
                    hashes.size(), refillThresholdPercent);
            int amountToGenerate = maxSize - hashes.size();

            if (amountToGenerate <= 0) {
                isRefilling.set(false);
                return;
            }

            hashGenerator.getHashesAsync(amountToGenerate)
                    .thenAccept(newHashes -> {
                        hashes.addAll(newHashes);
                        log.info("Cache successfully refilled with {} hashes. New size: {}",
                                newHashes.size(), hashes.size());
                    })
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to refill cache.", ex);
                        }
                        isRefilling.set(false);
                    });
        }
    }
}
