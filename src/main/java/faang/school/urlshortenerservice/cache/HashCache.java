package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Value("${hash_cache.capacity}")
    private int capacity;

    @Value("${hash_cache.min_size}")
    private double minSize;

    @Value("${hash_cache.amount_fill}")
    private double amountFill;

    private Queue<Hash> hashes;

    @PostConstruct
    public void init() {
        log.info("HashCache init start");
        hashes = new ArrayBlockingQueue<>(capacity);

        List<Hash> hashBatch = hashGenerator.generateBatchNotAsync();
        hashes.addAll(hashBatch);

        log.info("HashCache init end");
    }

    public Hash getHash() {
        Hash poll = hashes.poll();

        if (hashes.size() < capacity * minSize) {
            fillHashes();
        }

        return poll;
    }

    @Async
    public void fillHashes() {
        if (!running.get()) {
            running.set(true);

            List<Hash> hashBatch = hashRepository.getHashBatch((long) (amountFill * capacity));
            hashes.addAll(hashBatch);
            hashGenerator.generateBatch();

            running.set(false);

            log.info("HashCache filled");
        }
    }

}
