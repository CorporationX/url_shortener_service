package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${data.cache.pool-size}")
    private int capacity;

    @Value("${data.cache.fill-percent}")
    private int fillPercent;

    private final HashGenerator hashGenerator;
    private final ExecutorService hashExecutor;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();

    @PostConstruct
    private void init() {
        hashes.addAll(hashGenerator.getHashes());
    }

    public String getHash() {
        if (shouldFillCache() && isFilling.compareAndSet(false, true)) {
            hashExecutor.submit(() -> {
                try {
                    List<String> newHashes = hashGenerator.getHashes();
                    hashes.addAll(newHashes);
                } finally {
                    isFilling.set(false);
                }
            });
        }

        String hash = hashes.poll();
        if (hash == null) {
            throw new NoSuchElementException("Нет доступных хэшей");
        }

        return hash;
    }

    private boolean shouldFillCache() {
        return hashes.size() * 100 / capacity < fillPercent;
    }
}
