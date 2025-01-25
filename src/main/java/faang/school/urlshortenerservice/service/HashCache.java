package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.capacity}")
    private int capacity;
    @Value("${hash.cache.fill.percent}")
    private int fillPercent;
    private Queue<String> hashes;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private final HashService hashService;
    private final AsyncHashService asyncHashService;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashService.getHashes(capacity));
    }

    public String getHash() {
        if(hashes.size() / (capacity/100) < fillPercent && filling.compareAndSet(false, true)) {
            asyncHashService.getHashesAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> filling.set(false));
        }
        return hashes.poll();
    }
}
