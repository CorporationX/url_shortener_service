package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocalHash {
    private final HashGenerator hashGenerator;
    private final Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isEmpty = new AtomicBoolean(false);

    @Value(value = "${hash.local.minSize:200}")
    private int minSize;

    @PostConstruct
    private void init() {
        addHashes();
    }

    public String getHash() {
        if (concurrentQueue.size() < minSize && isEmpty.compareAndExchange(false, true)) {
            addHashes();
        }
        return concurrentQueue.poll();
    }

    private CompletableFuture<Void> addHashes() {
        return CompletableFuture.supplyAsync(hashGenerator::getHashes) //todo add executor
                .thenApply(concurrentQueue::addAll)
                .thenRun(() -> isEmpty.set(false));
    }
}
