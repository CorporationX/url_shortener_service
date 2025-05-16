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
    private final Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isEmpty = new AtomicBoolean(false);

    @PostConstruct
    private void init() {
        addHashes();
    }

    public String getHash() {
        if (concurrentQueue.size() < hashProperties.getSaving().getMinSize()
                && isEmpty.compareAndExchange(false, true)) {
            addHashes();
        }
        return concurrentQueue.poll();
    }

    private void addHashes() {
        CompletableFuture.supplyAsync(hashService::getHashes, getHashesPool)
                .thenApply(concurrentQueue::addAll)
                .thenRun(() -> isEmpty.set(false));
    }
}
