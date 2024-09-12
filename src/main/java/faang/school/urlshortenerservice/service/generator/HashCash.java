package faang.school.urlshortenerservice.service.generator;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCash {
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling;
    private final Queue<String> hashes;
    private final int capacity;
    private final int percentFill;
    private final int minValue;

    public HashCash(HashGenerator hashGenerator,
                    @Value("${data.hash.cash.capacity:10000}") int capacity,
                    @Value("${data.hash.cash.fill_percent:20}") int percentFill) {
        this.hashGenerator = hashGenerator;
        this.capacity = capacity;
        this.percentFill = percentFill;
        this.minValue = percentFill * capacity / 100;
        filling = new AtomicBoolean(false);
        hashes = new ArrayBlockingQueue<>(capacity);
    }

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (hashes.size() <= minValue) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync((int)(capacity * (1 - percentFill / 100.0)))
                        .thenAccept(hashes::addAll)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                System.err.println("Ошибка при получении хешей: " + ex.getMessage());
                            }
                            filling.set(false);
                        });
            }
        }

        return hashes.poll();
    }
}
