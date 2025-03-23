package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {

    private final int capacity;
    private final int minPercentageToFill;
    private final BlockingQueue<String> hashes;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean();

    @Autowired
    public HashCache(@Value("${services.cache.capacity}") int capacity,
                     @Value("${services.cache.minPercentageToFill}") int minPercentageToFill,
                     HashGenerator hashGenerator) {
        this.capacity = capacity;
        this.minPercentageToFill = minPercentageToFill;
        this.hashes = new ArrayBlockingQueue<>(capacity);
        this.hashGenerator = hashGenerator;
    }

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() throws InterruptedException {
        int threshold = (capacity * minPercentageToFill) / 100;
        if (hashes.size() < threshold && filling.compareAndSet(false, true)) {
            int needed = capacity - hashes.size();
            if (needed > 0) {
                hashGenerator.getHashesAsync(needed)
                        .thenAccept(list -> hashes.addAll(list.subList(0, Math.min(list.size(), needed))))
                        .exceptionally(ex -> {
                            filling.set(false);
                            return null;
                        })
                        .thenRun(() -> filling.set(false));
            } else {
                filling.set(false);
            }
        }
        return hashes.take();
    }
}