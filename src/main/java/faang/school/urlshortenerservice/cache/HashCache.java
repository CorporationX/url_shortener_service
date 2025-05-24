package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.properties.HashGenerationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerationProperties hashGenerationProperties;
    private final HashGenerator hashGenerator;
    private Queue<String> localHashes;
    private int capacity;
    private AtomicBoolean needReFill = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        capacity = hashGenerationProperties.getLocalHashCapacity();
        localHashes = new ArrayBlockingQueue<>(capacity);
        localHashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (needsRefillLocalHash()) {
            hashGenerator.getHashesAsync(capacity)
                    .thenAccept(localHashes::addAll)
                    .thenRun(() -> needReFill.set(false));
        }
        return localHashes.poll();
    }

    private boolean needsRefillLocalHash() {
        return ((double) localHashes.size() / capacity) * 100 < hashGenerationProperties.getMinQueuePercent()
                && needReFill.compareAndExchange(false,true);

    }
}