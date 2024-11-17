package faang.school.urlshortenerservice.util.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class HashCache {


    private final HashRepository hashRepository;
    private final HashCacheProperty cacheProperty;
    private final HashGenerator generator;

    private final Queue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private int threshold;

    @PostConstruct
    void initHashCache() {
        generator.generateBatch();
        int currentSize = hashQueue.size();
        threshold = (int) (cacheProperty.getMaxQueueSize() * (cacheProperty.getRefillPercent() / 100.0));
    }

    public String getHash() {
        int currentSize = hashQueue.size();

        if (currentSize < threshold && isRefilling.compareAndSet(false, true)) {
            //
        }
        return hashQueue.poll();
    }


}