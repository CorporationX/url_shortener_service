package faang.school.urlshortenerservice.local_cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocalCache {
    private final HashGenerator hashGenerator;
    private Queue<Hash> cache = new ConcurrentLinkedDeque<>();
    @Value("${hash.get_size}")
    private long getSize;
    @Value("${hash.min_cache_rep_size}")
    private long minSize;
    private AtomicBoolean aBoolean = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        for (int i = 0; i < 2; i++) {
            addNewHash();
        }
    }

    public String getCache() {
        if (cache.size() - 1 < minSize) {
            if (aBoolean.compareAndExchange(false, true)) {
                CompletableFuture.runAsync((this::addNewHash)).thenRun(() -> aBoolean.set(false));
            }
        }
        return cache.poll().getHash();
    }

    public void addNewHash() {
        cache.addAll(hashGenerator.findAndDelete());
    }
}
