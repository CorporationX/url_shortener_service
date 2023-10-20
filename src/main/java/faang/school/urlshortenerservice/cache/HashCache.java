package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${queue-size}")
    private int queueSize = 1;
    @Value("${percent}")
    private int fillPercent;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    public final Queue<String> hashes = new ArrayBlockingQueue<>(queueSize);

    @PostConstruct
    public void init() {
        List<String> generatedHashes = hashGenerator.getHashes(queueSize);
        for (String hash : generatedHashes) {
            if (hashes.offer(hash)) {
                log.warn("The element added in queue: {}", hash);
            } else {
                log.warn("Queue is full");
            }
        }
    }

    public String getHash() {
        if (hashes.size() / (queueSize / 100.0) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                fillHashes();
            }
        }
        return hashes.poll();
    }

    private void fillHashes() {
        hashGenerator.getHashesAsync(queueSize)
                .thenAccept(hashes::addAll)
                .thenRun(() -> filling.set(false));
    }
}
