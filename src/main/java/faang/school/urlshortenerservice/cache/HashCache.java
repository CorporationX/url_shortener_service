package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.async.AsyncConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity:10}")
    private int capacity;
    @Value("${hash.cache.fill-percent:2}")
    private int fillPercent;

    private AtomicBoolean filling;//  = new AtomicBoolean(false);
    private Queue<Hash> hashes;//  = new ArrayBlockingQueue<>(capacity);


    @PostConstruct
    public void init() {
        filling = new AtomicBoolean(false);
        hashes = new ArrayBlockingQueue<>(capacity);
        log.info("иницилизация");
        hashGenerator.getHashesAsync(capacity).thenAccept(hashes::addAll);
        log.info("проиницилизировался");
    }

    public Hash getHash() {
        if ((hashes.size() / (capacity / 100) < fillPercent)) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }

}