package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${hashcache.max-size}")
    private int maxSize;

    @Value("${hashcache.fill-threshold}")
    private int fillThreshold;

    private final HashRepository hashRepository;
    private final ExecutorService executorService;

    private final Queue<String> hashCache = new ConcurrentLinkedQueue<>();

    private final Lock lock = new ReentrantLock();
    private volatile boolean isCacheRefilling = false;

    public String getHash() {
        if (hashCache.size() > (maxSize * fillThreshold / 100)) {
            return getRandomHash();
        }

        if (!isCacheRefilling && lock.tryLock()) {
            try {
                isCacheRefilling = true;
                executorService.submit(this::fillCache);
            } catch (Exception e) {
                log.error("Error while trying to refill cache", e);
            } finally {
                lock.unlock();
            }
        }

        return hashCache.peek();
    }

    private void fillCache() {
        log.info("Starting to fill hash cache...");
        try {
            List<String> newHashes = hashRepository.getHashBatch(10);
            hashCache.addAll(newHashes);
            generateMoreHashes();
        } catch (Exception e) {
            log.error("Error while filling hash cache", e);
        } finally {
            isCacheRefilling = false;
        }
    }

    private void generateMoreHashes() {
        executorService.submit(() -> {
            try {
                log.info("Starting to generate more hashes...");
                List<String> generatedHashes = generateHashes(10);

                List<Hash> hashEntities = new ArrayList<>();
                for (String generatedHash : generatedHashes) {
                    Hash hashEntity = new Hash(generatedHash);
                    hashEntities.add(hashEntity);
                }

                hashRepository.saveAll(hashEntities);

                hashCache.addAll(generatedHashes);
            } catch (Exception e) {
                log.error("Error while generating more hashes", e);
            }
        });
    }

    private List<String> generateHashes(int count) {
        List<String> newHashes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            newHashes.add(generateRandomHash());
        }
        return newHashes;
    }

    private String generateRandomHash() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String getRandomHash() {
        Random random = new Random();
        int size = hashCache.size();
        int randomIndex = random.nextInt(size);
        int currentIndex = 0;
        for (String hash : hashCache) {
            if (currentIndex == randomIndex) {
                return hash;
            }
            currentIndex++;
        }
        return hashCache.peek();
    }
}