package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
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
    @Value("%{hash.cache.capacity :10000}")
    private int capacity;
    @Value("%{hash.cache.fill.percent :10}")
    private int fillPercent;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);

    @PostConstruct
    public void init(){
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash(){
        if(filling.compareAndSet(false, true)) {
            if (hashes.size() / (capacity / 100.0) < fillPercent) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }
}
