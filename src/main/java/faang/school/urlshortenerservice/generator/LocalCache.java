package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalCache {

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.fill.percent}")
    private int fillPercent;

    @Value("${hash.thread.pool.core}")
    private int corePoolSize;

    private final Queue<String> hashes = new ConcurrentLinkedQueue<>();
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(corePoolSize);
            log.info("ExecutorService initialization started with thread pool size: {}", corePoolSize);

            hashes.addAll(hashGenerator.getHashes(capacity));
            log.info("LocalCache initialization started with {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Error initializing LocalCache ", e);
            throw e;
        }
    }

    public String getHash() {
        if (capacity / hashes.size() * 100 < fillPercent &&
                filling.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> filling.set(false));
        }
        return hashes.poll();
    }
}