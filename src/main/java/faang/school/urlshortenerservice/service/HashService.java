package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private final HashRepository hashRepository;
    private final HashConfig hashConfig;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private final static String STORAGE_TYPE = "database";

    private final AtomicBoolean blocked = new AtomicBoolean(false);

    @Transactional
    public List<String> getFreeHashes(long count) {
        refillStorageIfDepleted();
        return hashRepository.getFreeHashesLocked(count);
    }

    @Transactional
    public void returnHashesFromExpiredUrls(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }

    public void refillStorageIfDepleted() {
        boolean depletedNotFilling = checkStorageSize() && blocked.compareAndSet(false, true);
        if (depletedNotFilling) {
            refillStorageFunctionAsync(hashGenerator::generateBatch, STORAGE_TYPE, blocked);
        }
    }

    public boolean checkStorageSize() {
        return hashRepository.count() < hashConfig.getCurrentStorageFullness();
    }

    public void refillStorageFunctionAsync(Runnable refillFunction, String storageType, AtomicBoolean blocker) {
        threadPoolTaskExecutor.submit(() -> {
            try {
                log.info("Starting to refill {}", storageType);
                refillFunction.run();
            } catch (Exception e) {
                log.error("Failed to refill {}",storageType, e);
            } finally {
                blocker.set(false);
            }
        });
    }
}
