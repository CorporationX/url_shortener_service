package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCache {
    private final HashGenerator hashGenerator;
    private final LocalCacheProperties properties;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private Queue<Hash> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(properties.getCapacity());
        hashes.addAll(hashGenerator.getHashes(properties.getCapacity()));
        log.info("LocalHash initialization completed.");
    }

    public String getHash() {
        if (hashes.size() < (properties.getCapacity() * properties.getFillPercentage() / 100)) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(properties.getCapacity())
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        }
        Hash hash = hashes.poll();
        if (hash == null) {
            throw new RuntimeException("No hashes available in the cache");
        }
        return hash.getHash();
    }
}
