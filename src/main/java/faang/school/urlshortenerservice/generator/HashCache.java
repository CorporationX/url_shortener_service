package faang.school.urlshortenerservice.generator;


import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
            log.info("hashes all = {} ",hashes.size());
            for (String hash : hashes) {
                log.info("Hash: {}", hash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 0 * * * *")//запускает задачу каждый час")
    private String getHash() {
        if ((hashes.size() * (capacity / 100.0)) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                executorService.submit(() -> {
                    hashGenerator.getHashesAsync(capacity)
                            .thenAccept(hashes::addAll)
                            .thenRun(() -> filling.set(false));
                });
            }
        }
        return hashes.poll();
    }
}