package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${application.hashCache.capacity:1000}")
    private int capacity;

    @Value("${application.hashCache.fillFactor:20}")
    private short fillFactor;
    private AtomicBoolean filling;
    private final Queue<String> hashes;
    private final HashGenerator generator;

    @PostConstruct
    public void init() {
        generator.generateBatch().thenAccept(hashes::addAll);
    }

    public String getHash() {
        if (isRunningOut()) {
            if (filling.compareAndSet(false, true)) {
                fillHashes();
            }
        }
        return hashes.poll();
    }

    private void fillHashes() {
        generator.generateBatch()
                .thenAccept(hashes::addAll)
                .thenRun(() -> filling.set(false));
    }

    private boolean isRunningOut() {
        return fillFactor <= hashes.size() / (capacity * 100);
    }
}
