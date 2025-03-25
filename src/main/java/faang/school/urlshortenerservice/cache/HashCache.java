package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.properties.ThreadPoolProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.GeneratorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache implements Cache {

    private final ThreadPoolProperties threadPoolProperties;
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final GeneratorService generatorService;

    private ConcurrentLinkedQueue<String> hashQueue;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashQueue = new ConcurrentLinkedQueue<>();
        generatorService.generateHashBatch();
        hashQueue.addAll(hashRepository.getHashBatch());
    }

    public String getHash() {
        if (shouldRefillQueue()) {
            refillCache();
        }
        return hashQueue.poll();
    }

    private boolean shouldRefillQueue() {
        return hashQueue.size() > threadPoolProperties.getCacheSize() * threadPoolProperties.getRefillThreshold();
    }

    private void refillCache() {
        if (isRefilling.compareAndSet(false, true)) {
            executorService.execute(() -> {
                try {
                    hashQueue.addAll(hashRepository.getHashBatch());
                    generatorService.generateHashBatch();
                } finally {
                    isRefilling.set(false);
                }
            });
        }
    }
}
