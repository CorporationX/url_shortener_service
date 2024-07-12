package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repositoy.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;

    private final HashRepository hashRepository;

    private final Queue<Hash> cache = new ArrayBlockingQueue<>(10000);

    @Value("${}")
    private long maxCapacity;

    @Value("${}")
    private long fillPercent;

    @Value("${}")
    private long capacity;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void unit() {
        cache.addAll(hashGenerator.getHashes(maxCapacity));
    }

    public Hash getHash() {
        if (cache.size() / (maxCapacity / 100) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashes(capacity).thenAccept(cache::addAll)
                        .thenRun(filling.set(false));
            }
        }
        return cache.poll();
    }
}
