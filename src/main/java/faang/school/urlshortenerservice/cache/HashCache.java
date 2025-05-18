package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final TaskExecutor hashTaskExecutor;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final Queue<String> hashes = new LinkedBlockingQueue<>();

    @Value("${hash.cache.capacity:10000}")
    private int capacity;

    @Value("${hash.cache.fill.percent:20}")
    private int fillPercent;

    @Value("${hash.cache.fill.timeout:180}")
    private int fillTimeout;

    @PostConstruct
    public void init() {
        refill();
    }

    @Transactional
    public String getHash() {
        String hash = hashes.poll();
        if (hash == null) {
            log.info("Hash cache is empty, refilling...");
            refillAsync();
            throw new IllegalStateException("Hash cache is empty");
        }
        if (hashes.size() < capacity * fillPercent / 100) {
            log.info("Hash cache is almost empty, refilling...");
            refillAsync();
        }
        return hash;
    }

    private void refill() {
        int missing = capacity - hashes.size();
        if (missing > 0) {
            log.info("Refilling hash cache with {} missing hashes", missing);
            if (isRefilling.compareAndSet(false, true)) {
                try {
                    List<String> newHashes = hashGenerator.getHashes(missing);
                    hashes.addAll(newHashes);
                } catch (Exception e) {
                    log.error("Error cache refill: {}", e.getMessage());
                } finally {
                    isRefilling.set(false);
                }
            }
            log.info("Finished refilling hash cache");
        }
    }

    private void refillAsync() {
        hashTaskExecutor.execute(this::refill);
    }
}
