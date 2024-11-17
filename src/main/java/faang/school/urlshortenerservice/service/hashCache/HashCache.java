package faang.school.urlshortenerservice.service.hashCache;

import faang.school.urlshortenerservice.config.hash.HashProperties;
import faang.school.urlshortenerservice.service.hashGenerator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;

    private final int fillPercent = hashProperties.getFillPercent();
    private final int capacity = hashProperties.getCapacity();
    private final ArrayBlockingQueue<String> hashQueue = new ArrayBlockingQueue<>(capacity);
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void BeforeEach() {
        hashQueue.addAll(hashGenerator.getHashes(capacity));
    }


    public String getHash() {
        boolean exceededLimit = hashQueue.size() / (capacity / 100.0) < fillPercent;

        if (exceededLimit) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity - hashQueue.size()).
                        thenAccept(hashQueue::addAll)
                        .thenRun(() -> filling.set(false));

            }
        }
        return hashQueue.poll();
    }
}
