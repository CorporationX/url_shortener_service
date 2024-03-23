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

    @Value("${hash.cache.capacity:1000}")
    private int capacity;
    @Value("${hash.cache.fill-percent:2}")
    private int fillPercent;
    private AtomicBoolean filling;
    private Queue<Hash> hashes;


    @PostConstruct
    public void init() {
        filling = new AtomicBoolean(false);
        hashes = new ArrayBlockingQueue<>(capacity);
        hashGenerator.getHashesAsync(capacity).thenAccept(hashes::addAll);
        log.info("Инициализировали кэш хэшей: {}", capacity);
    }

    public Hash getHash() {
        if ((hashes.size() / (capacity / 100) < fillPercent)
                && (filling.compareAndSet(false, true))) {
            log.info("Заполняем кэш хэшей: {}", capacity);
            hashGenerator.getHashesAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> filling.set(false));

        }
        log.info("Получили хэши из кэша: {}", hashes.size());
        return hashes.poll();
    }

}