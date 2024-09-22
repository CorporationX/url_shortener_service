package faang.school.urlshortenerservice.config.cashe;

import faang.school.urlshortenerservice.config.hashegeneration.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator generator;
    private final HashService hashService;

    private LinkedBlockingQueue<String> queue;
    private AtomicBoolean isFilling;

    @Value("${spring.queue.size}")
    private int queueCapacity;
    @Value("${spring.queue.minimum-size}")
    private int minimumSize;

    public String getHash() {
        if (queue.size() <= minimumSize) {
            if (isFilling.compareAndSet(false, true)) {
                try {
                    if (queue.size() <= minimumSize) {
                        if (hashService.getCount() < queueCapacity) {
                            generator.generateBatch();
                        }
                    }
                } finally {
                    isFilling.set(false);
                }
            }
            fillCache();
        }
        return queue.poll();
    }

    @PostConstruct
    private void createCache() {
        isFilling = new AtomicBoolean(false);
        queue = new LinkedBlockingQueue<>(queueCapacity);
        generator.generateBatch();
        fillCache();
    }

    private void fillCache() {
        List<String> hashes = hashService.getHashBatch();
        generator.fillQueueAsync(queue, hashes);
    }
}
