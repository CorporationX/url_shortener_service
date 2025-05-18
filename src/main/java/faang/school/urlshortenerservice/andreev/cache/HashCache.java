package faang.school.urlshortenerservice.andreev.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${shortener.async.pool.queue-capacity}:1000")
    private int capacity;

    @Value("${shortener.hash.fill.percent}:20")
    private int fillPercent;

    private final Queue<String> hashes = new LinkedBlockingDeque<>(capacity);
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        checkFill();
        return hashes.poll();
    }

    private void checkFill() {
        if(hashes.size() / (capacity / 100.0) < fillPercent) {
            if(isFilling.compareAndSet(false, true)) {
                log.info("Hash less than {} percent left", fillPercent);
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> isFilling.set(false));
                log.info("Hashes added to the cache: {}", hashes);
            }
        }
    }
}
