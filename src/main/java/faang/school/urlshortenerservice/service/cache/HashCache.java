package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Setter
@Slf4j
@Component
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();

    private int capacity;
    private int minLoadFactor;

    public HashCache(HashGenerator hashGenerator, HashRepository hashRepository,
                     @Value("${hash.local_cache.capacity}") int capacity,
                     @Value("${hash.local_cache.min_load_factor}") int minLoadFactor) {
        this.hashGenerator = hashGenerator;
        this.hashRepository = hashRepository;
        this.capacity = capacity;
        this.minLoadFactor = minLoadFactor;

        initHashes();
    }

    public String getHash() {
        if (!running.get() && !isEnoughHashes() && running.compareAndSet(false, true)) {
            int freeSize = capacity - hashes.size();
            hashGenerator.getHashesAsync(freeSize)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> running.set(false));
            hashGenerator.generateHashesAsync();
        }
        return hashes.poll();
    }

    private boolean isEnoughHashes() {
        int loadFactor = (hashes.size() * 100) / capacity;
        return loadFactor > minLoadFactor;
    }

    private void initHashes() {
        long size = hashRepository.count();
        List<String> initHashes;
        if (size < capacity) {
            initHashes = hashGenerator.generateHashes(capacity);
        } else {
            initHashes = hashGenerator.getHashes(capacity);
        }
        hashes.addAll(initHashes);
        hashGenerator.generateHashesAsync();
    }
}
