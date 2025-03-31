package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.refill_threshold}")
    private double refillThreshold;

    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        refillHashes();
    }

    public String getHash() {
        try {
            return hashQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for hash");
        }
    }

    @Transactional
    public void refillHashes() {
        List<String> dbHashes = getHashesFromDb(capacity);
        hashQueue.addAll(dbHashes);

        if (needRefill()) {
            hashGenerator.generateBatch();
            List<String> newHashes = getHashesFromDb(capacity);
            hashQueue.addAll(newHashes);
        }
    }

    public int getCapacity() {
        return hashQueue.size();
    }

    private List<String> getHashesFromDb(int batchSize) {
        return hashRepository.getHashBatch(batchSize)
            .stream()
            .map(Hash::getHash)
            .toList();
    }

    private boolean needRefill() {
        return hashQueue.size() < capacity * refillThreshold;
    }
}