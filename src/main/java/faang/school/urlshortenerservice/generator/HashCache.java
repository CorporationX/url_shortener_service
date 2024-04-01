package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;
    @Value("${hash.cache.capacity:10000}")
    private int capacity;
    @Value("${hash.cache.fill.percent:20}")
    private int fillPercent;
    private AtomicBoolean filling;
    private Queue<Hash> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
        log.info("создали и загрузили {} хешей в кэш", capacity);
    }

    public Hash getHash() {
        if ((hashes.size() * 100.0 / capacity) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
                log.info("ещё создали и загрузили {} хешей в кэш", capacity);
            }
        }
        return hashes.poll();
    }
}
