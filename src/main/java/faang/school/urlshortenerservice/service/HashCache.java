package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${queue-size}")
    private int queueSize;
    @Value("${percent}")
    private int fillPercent;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    public final Queue<String> hashes = new ArrayBlockingQueue<>(queueSize);

    @PostConstruct
    public void init() {
        List<String> generatedHashes = hashGenerator.getHashes(queueSize).join();
        for (String hash : generatedHashes) {
            if (hashes.offer(hash)) {
                log.warn("The element added to queue: {}", hash);
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
        hashGenerator.getHashes(queueSize)
                .thenAccept(c -> hashes.addAll(c))
                .thenRun(() -> filling.set(false));
    }
}