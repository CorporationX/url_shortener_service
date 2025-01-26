package faang.school.urlshortenerservice.localcache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${hash.cash.capaсity}")
    private int capacity;

    @Value("${hash.cash.perсent}")
    private int fillPercent;

    private AtomicBoolean filling;

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (hashes.size() / (capacity / 100.0) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity).thenAccept(hashes::addAll);
                filling.set(false);
            }
        }
        return hashes.poll();
    }
}
