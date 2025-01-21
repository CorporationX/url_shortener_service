package faang.school.urlshortenerservice.generator;


import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
@Slf4j
@Component
@Data
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;

    @Value("${hash.cache.capacity:5}")
    private int capacity;

    @Value("${hash.cache.fill.percent:20}")
    private int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private Queue<String> hashes;

    @PostConstruct
    private void init() {
        this.hashes = new ArrayBlockingQueue<>(capacity);
        try {
            hashes.addAll(hashGenerator.getHashBatch(capacity));
            log.info("hashes all = {} ", hashes.size());
        } catch (Exception e) {
            log.error("Error {}",e);
        }
    }
    public String getHash() {
        if ((hashes.size() * (capacity / 100.0)) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                executorService.submit(() ->
                {
                    List<String> batch = hashGenerator.getHashBatch(capacity);
                    synchronized (hashes) {
                        hashes.addAll(batch);
                    }
                    filling.set(false);
                });
            }
        }
        return hashes.poll();
    }
}