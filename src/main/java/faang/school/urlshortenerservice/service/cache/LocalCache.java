package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCache {

    private final HashService hashService;

    @Value("${hash.cache.capacity:500}")
    private int capacity;

    @Value("${hash.cache.fill_percentage:25}")
    private float fillPercentage;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private BlockingQueue<Hash> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashService.getHashes(capacity));
    }

    @SneakyThrows
    public String getHash() {
        if (hashes.size() < (capacity * fillPercentage)) {
            if (isFilling.compareAndSet(false, true)) {
                hashService.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> isFilling.set(false));
                log.info("Request getHashes");
            }
        }

        return hashes.take().getHash();
    }
}
