package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.cache.LocalCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashGeneratorAsync generatorAsync;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final LocalCache localCache;

    @Value("${spring.url-shortener.hash.batch-size}")
    private int batchSize;
    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(batchSize);
        hashes.addAll(hashGenerator.getHashes(batchSize));
    }

    public String getHash() {
        if (!localCache.hashSizeValidate()) {
            if (isFilling.compareAndSet(false, true)) {
                try {
                    CompletableFuture<List<String>> futureHashes = generatorAsync.getHashesAsync(batchSize);
                    futureHashes
                            .thenAccept(hashes::addAll)
                            .thenRun(() -> isFilling.set(false));

                    localCache.saveHashesInCache(futureHashes.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return hashes.poll();
    }
}
