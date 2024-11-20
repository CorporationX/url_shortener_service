package faang.school.urlshortenerservice.cache;

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

    @Value("${hash.cache.capacity:10000}")
    private int capacity;

    @Value("${hash.cache.fill.percent:20}")
    private int fillPercent;

    private AtomicBoolean filling = new AtomicBoolean(false);

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (hashes.size() / (capacity / 100.0) < fillPercent){
            if (filling.compareAndSet(false,true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }
}