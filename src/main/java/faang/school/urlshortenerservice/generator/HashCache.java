package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Data
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;

    //@Value("${hash.cache.capacity:10000}")
    private int capacity = 10000;

    @Value("${hash.fill.percent:20}")
    private int fillPercent;

    private AtomicBoolean filling = new AtomicBoolean(false);

    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);

    @PostConstruct
    public void init() {
        log.info("Hashes start size = " + hashes.size());
        hashes.addAll(hashGenerator.getHashes(capacity));
        log.info("Hashes end size = " + hashes.size());
    }

    public String getHash() {
        if (hashes.size() * 100 / capacity < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() ->  filling.set(false));
            }
        }
        String hash = hashes.poll();
        log.info("Got hash = " + hash);
        return hash;
    }
}
