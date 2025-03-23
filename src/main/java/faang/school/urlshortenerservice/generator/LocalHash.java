package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocalHash {

    private final HashService hashService;

    @Value("${url-shortener.hash.capacity}")
    private int capacity;

    @Value("${url-shortener.hash.fill-percent}")
    private int fillPercent;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    private BlockingQueue<Hash> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashService.getHashes(capacity));
    }

    public Hash getHash() {
        if (hashes.size() / (capacity / 100) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashService.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }
}
