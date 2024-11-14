package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Component
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${cache.capacity:10000}")
    private int capacity;

    private final AtomicBoolean generateIsProcessing = new AtomicBoolean(false);

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);

        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (hashes.isEmpty() || hashes.size() / capacity * 100.0 < 20.0) {
            if (generateIsProcessing.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> generateIsProcessing.set(false));
            }
        }

        return hashes.poll();
    }

}
