package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    @Value("${hash.capacity}")
    private int capacity = 50;
    @Value("${hash.check-percent}")
    private int percent;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private final Queue<Hash> hashes = new ArrayBlockingQueue<>(capacity);

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        generateHashCash();
        return hashes.poll().getHash();
    }

    public void generateHashCash() {
        if (hashes.size() * 100 / capacity < percent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenApply(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
    }
}