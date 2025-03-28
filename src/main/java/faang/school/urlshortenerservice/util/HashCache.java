package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Slf4j
@Service("utilHashCache")
public class HashCache {
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private final HashGenerator hashGenerator;

    private final ConcurrentLinkedQueue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @Value("${cache.hash.size:1000}")
    private int maxCacheSize;

    @Value("${cache.hash.threshold-percent:20}")
    private int thresholdPercent;

    @Value("${cache.hash.batch-size:500}")
    private int batchSize;

    @PostConstruct
    public void init() {
        refillCache();
    }

    public String getHash() {
        checkAndRefillIfNeeded();
        return hashQueue.poll();
    }

    public List<String> getHashCache(List<Long> numbers) {
        checkAndRefillIfNeeded();
        return numbers.stream()
                .map(n -> hashQueue.poll())
                .filter(Objects::nonNull)
                .toList();
    }

    private void checkAndRefillIfNeeded() {
        int threshold = maxCacheSize * thresholdPercent / 100;
        if (hashQueue.size() < threshold && isRefilling.compareAndSet(false, true)) {
            log.info("Cache below threshold ({}%). Starting async refill.", thresholdPercent);
            executorService.submit(this::refillCache);
        }
    }

    private void refillCache() {
        try {
            log.info("Starting cache refill. Current size: {}", hashQueue.size());

            List<Long> availableHashes = hashRepository.findTopN(batchSize);

            if (!availableHashes.isEmpty()) {
                availableHashes.forEach(number -> hashQueue.offer(number.toString()));
            }

            hashGenerator.generateBatch();

            log.info("Cache refill completed. New size: {}", hashQueue.size());
        } catch (Exception e) {
            log.error("Error during hash cache refill", e);
        } finally {
            isRefilling.set(false);
        }
    }
}
