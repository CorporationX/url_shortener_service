package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repo.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCache {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.fill.percent}")
    private int fillPercent;

    private AtomicBoolean filling;
    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        filling = new AtomicBoolean(false);
        hashes = new ArrayBlockingQueue<>(capacity);
        fillCache();
    }

    public String getHash() {
        if (needToFillCache()) {
            tryFillCache();
        }
        return hashes.poll();
    }

    private boolean needToFillCache() {
        return (hashes.size() / (double) capacity * 100) < fillPercent;
    }

    private void tryFillCache() {
        if (filling.compareAndSet(false, true)) {
            log.info("Starting to fill hash cache");

            try {
                List<String> batch = hashRepository.getHashBatch(capacity);
                hashes.addAll(batch);
                log.info("Added {} hashes to cache", batch.size());
            } catch (Exception e) {
                log.error("Failed to fill hash cache", e);
            } finally {
                filling.set(false);
            }
            hashGenerator.generateBatch()
                    .thenRun(() -> log.info("New batch of hashes generated"))
                    .exceptionally(ex -> {
                        log.error("Failed to generate new hashes", ex);
                        return null;
                    });
        }
    }

    private void fillCache() {
        try {
            List<String> initialHashes = hashRepository.getHashBatch(capacity);
            hashes.addAll(initialHashes);
            log.info("Initial cache filled with {} hashes", initialHashes.size());
        } catch (Exception e) {
            log.error("Failed to initialize hash cache", e);
        }
        hashGenerator.generateBatch();
    }
}