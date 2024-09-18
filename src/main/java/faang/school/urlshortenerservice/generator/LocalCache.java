package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalCache {

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.fill.percent}")
    private int fillPercent;

    @Resource(name = "taskExecutor")
    private final ThreadPoolTaskExecutor executor;
    private final Queue<String> hashes = new ConcurrentLinkedQueue<>();
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        try {
            hashes.addAll(hashGenerator.getHashes(capacity));
            log.info("LocalCache initialization started with {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Error initializing LocalCache ", e);
            throw e;
        }
    }

    public String getHash() {
        if (hashes.size() / capacity * 100 < fillPercent &&
                filling.compareAndSet(false, true)) {
            log.info("Local cache less than 20 percent");
            executor.execute(this::fillHandler);
            fillHandler();
        }
        return hashes.poll();
    }

    public void fillHandler() {
        hashGenerator.getHashesAsync(capacity)
                .thenAccept(hashes::addAll)
                .exceptionally(ex -> {
                    log.error("Error initializing LocalCache ", ex);
                    return null;
                })
                .thenRun(() -> filling.set(false));

    }
}