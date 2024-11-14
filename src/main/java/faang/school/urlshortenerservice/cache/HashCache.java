package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash.cache.size}")
    private int cacheSize;

    @Value("${hash.cache.percent}")
    private int percent;

    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private BlockingQueue<String> cacheQueue;
    private final AtomicBoolean isFilled = new AtomicBoolean(false);
    private double hasPercent;

    @PostConstruct
    private void init() {
        hasPercent = (double) (cacheSize * percent) / 100;
        cacheQueue = new LinkedBlockingQueue<>(cacheSize);
    }

    public String getHash() {
        if (cacheQueue.size() > hasPercent) {
            return cacheQueue.poll();
        } else {
            if (isFilled.compareAndSet(false, true)) {
                synchronized (cacheQueue) {
                    executorService.execute(() -> {
                        int batchSize = cacheSize - cacheQueue.size();
                        List<String> hashes = hashRepository.getHashBatch(batchSize);
                        hashes.forEach(cacheQueue::add);
                        hashGenerator.generateBatch();
                        isFilled.set(false);
                    });
                }
            }

            while (cacheQueue.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            return cacheQueue.poll();
        }
    }

}
