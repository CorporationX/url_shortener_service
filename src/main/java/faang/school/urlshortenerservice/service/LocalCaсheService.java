package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Component
public class LocalCaсheService {

    private final HashGeneratorService hashGeneratorService;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.fill-percent}")
    private double fillPercent;

    private Queue<String> hashes;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        if (capacity <= 0) {
            throw new IllegalStateException("Capacity must be positive");
        }
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGeneratorService.getHashes(capacity));
    }

    public String getHash() {
        if (shouldRefillHashes() && filling.compareAndSet(false, true)) {
            hashGeneratorService.getHashesAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .whenComplete((res, ex) -> filling.set(false));
        }
        return hashes.poll();
    }

    boolean shouldRefillHashes() {
        return ((double) hashes.size() / capacity) < fillPercent;
    }
}