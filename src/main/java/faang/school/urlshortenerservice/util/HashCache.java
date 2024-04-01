package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Data
public class HashCache {

    private BlockingQueue<String> cache;
    private AtomicBoolean isFilling = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final Executor cachedThreadPool;

    @Value("${url-shortener-service.queueSize}")
    private int queueSize;
    @Value("${url-shortener-service.fillPercent}")
    private int fillPercent;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(queueSize);
        fillCache();
    }

    public void fillCache() {
        int freeCache = queueSize - cache.size();
        hashGenerator.generateBatch();
        cache.addAll(hashRepository.getHashBatch(freeCache).stream()
                .map(Hash::getHash).toList());
        isFilling.set(false);
    }

    public String getHash() {
        if ((cache.remainingCapacity() / cache.size()) <= fillPercent) {
            if (isFilling.compareAndSet(false, true)) {
                cachedThreadPool.execute(this::fillCache);
            }
        }
        return cache.poll();
    }

    @Transactional
    public void saveHashes(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }
}