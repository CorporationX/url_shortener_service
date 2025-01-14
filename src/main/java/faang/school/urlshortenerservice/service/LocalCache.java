package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocalCache {
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity:1000}")
    private int capacity;

    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);

    @Value("${hash.cache.fill-percent:20}")
    private int fillPercent;

    private AtomicBoolean filling;

    @PostConstruct
    public void init(){
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (getCurPercent() < fillPercent) {
            if(filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }

    private int getCurPercent() {
        return hashes.size() / capacity * 100;
    }
}
