package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Setter
@Slf4j
@RequiredArgsConstructor
@Component
public class HashCache {
    private final HashGenerator hashGenerator;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Queue<String> hashes;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.min_load_factor}")
    private int minLoadFactor;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        List<String> initHashes = hashGenerator.getHashes(capacity);
        hashes.addAll(initHashes);
    }

    public String getHash() {
        if (running.compareAndSet(false, true) && !isEnoughHashes()) {
            int freeSize = capacity - hashes.size();
            hashGenerator.getHashesAsync(freeSize)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> running.set(false));
        }
        return hashes.poll();
    }

    private boolean isEnoughHashes() {
        int loadFactor = (hashes.size() * 100) / capacity;
        return loadFactor > minLoadFactor;
    }
}
