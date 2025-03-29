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
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity}") private int capacity;
    @Value("${hash.cache.refill_threshold}") private double refillThreshold;

    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        refillHashes();
    }

    public String getHash() {
        return pollHash();
    }

    @Transactional
    public void refillHashes() {
        log.info("before getHashBath {}", TransactionSynchronizationManager.isActualTransactionActive());
        List<String> dbHashes = hashRepository.getHashBatch(capacity)
            .stream()
            .map(Hash::getHash)
            .toList();

        hashQueue.addAll(dbHashes);

        if (needRefill()) {
            hashGenerator.generateBatch();
            List<String> newHashes = hashRepository.getHashBatch(capacity)
                .stream()
                .map(Hash::getHash)
                .toList();
            hashQueue.addAll(newHashes);
        }
    }

    public int getCapacity(){
        return hashQueue.size();
    }

    private boolean needRefill() {
        return hashQueue.size() < capacity * refillThreshold;
    }

    private String pollHash() {
        try {
            return hashQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for hash");
        }
    }
}