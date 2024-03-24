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

@Service
@RequiredArgsConstructor
@Data
public class HashCache {

    private BlockingQueue<Hash> cache;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final Executor cachedThreadPool;

    @Value("${url-shortener-service.hashSize}")
    private int hashSize;
    private int fillPercent = 20;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(hashSize);
        addCache();
        System.out.println(cache.size());
    }

    public void addCache() {
        int freeCache = hashSize - cache.size();
        hashGenerator.generateBatch(freeCache);
        cache.addAll(hashRepository.getHashBatch(freeCache));
    }

    public Hash getHash() {
        if ((cache.remainingCapacity() / cache.size()) <= fillPercent) {
            cachedThreadPool.execute(this::addCache);
        }
        return cache.poll();
    }

    @Transactional
    public void saveHashes(List<String> hashesStrings) {
        List<Hash> hashes = new ArrayList<>();
        for (String str : hashesStrings) {
            hashes.add(Hash.builder()
                    .hash(str)
                    .build());
        }
        hashRepository.saveAll(hashes);
    }
}