package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocalHash {
    private final HashService hashService;
    private final HashProperties hashProperties;
    private final ExecutorService getHashesPool;
    private volatile Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
    private volatile AtomicBoolean isLow = new AtomicBoolean(false);

    @PostConstruct
    private void init() {
        addHashes();
    }

    public String getHash() {
        if (concurrentQueue.size() < hashProperties.getSaving().getMinSize()
                && !isLow.compareAndExchange(false, true)) {
            CompletableFuture<Void> addHashesFuture = addHashes();

            if (concurrentQueue.isEmpty()) {
                addHashesFuture.join();
            }
        }
        return concurrentQueue.poll();
    }

    private CompletableFuture<Void> addHashes() {
        return CompletableFuture.supplyAsync(hashService::getHashes, getHashesPool)
                .thenAccept(concurrentQueue::addAll)
                .thenRun(() -> isLow.set(false));
    }
}
