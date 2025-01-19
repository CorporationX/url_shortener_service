package faang.school.urlshortenerservice.local_cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.TransactionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final TransactionService transactionService;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    @Value("${hashCache.cacheSize}")
    private int cacheSize;
    @Value("${hashCache.percent}")
    private int percent;
    private Queue<String> cache;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        cache.addAll(transactionService.saveHashBatch(cacheSize));
    }

    public String getHash() {
        if (!isAtLeast20PercentLeft() && isProcessing.compareAndExchange(false, true)) {
            hashGenerator.getHashBatch(cacheSize).thenAccept(cache::addAll)
                    .thenRun(() -> isProcessing.set(false));
        }
        return cache.poll();
    }

    private boolean isAtLeast20PercentLeft() {
        return cache.size() <= (cacheSize * percent) / 100;
    }

}
