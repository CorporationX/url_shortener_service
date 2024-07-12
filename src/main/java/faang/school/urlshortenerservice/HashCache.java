package faang.school.urlshortenerservice;

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

    private final Queue<Hash> hashes = new ArrayBlockingQueue<>(10000);

    @Value("${}")
    private long maxCapacity;

    @Value("${}")
    private long fillPercent;

    @Value("${}")
    private long capacity;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void unit() {
        hashQueue.addAll(hashRepository.getHashBatch(maxCapacity));
    }

    public Hash getHash() {
        if (hashes.size() / (maxCapacity / 100) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashRepository.getHashBatch(capacity).thenAccept(hashes::addAll)
                        .thenRun(filling.set(false));
                hashGenerator.generateBatch();
            }
        }
        return hashQueue.poll();
    }
}
