package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.config.HashConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private static final int HASH_GENERATION_LOCK_ID = 17;

    private final HashConfig hashConfig;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor executor;

    @Transactional
    public void refillHashStorage() {
        List<String> newHashValues = hashGenerator.generateHashes(hashConfig.getStorage().getSize());
        List<Hash> newHashes = newHashValues.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(newHashes);
        log.info("{} hashes generated", newHashes.size());
    }

    @Transactional
    public List<String> getFreeHashes(long count) {
        List<String> freeHashes = hashRepository.getFreeHashBatchWithLockAndDelete(count);
        boolean lessThanNeeded = freeHashes.size() < hashConfig.getStorage().getSize();
        if (lessThanNeeded) {
            int missingCount = hashConfig.getStorage().getSize() - freeHashes.size();
            List<String> missingHashes = hashGenerator.generateHashes(missingCount);
            freeHashes.addAll(missingHashes);
        }

        boolean needRefill = lessThanNeeded || hashRepository.count() < hashConfig.getStorageUpdateCount();

        if (needRefill) {
            refillStorageAsync();
        }
        return freeHashes;
    }

    private void refillStorageAsync() {
        executor.submit(() -> {
            boolean successfullyLockedByCurrentThread;
            try {
                log.info("Attempting to acquire advisory lock for storage refill...");
                successfullyLockedByCurrentThread = hashRepository.tryLock(HASH_GENERATION_LOCK_ID);
                if (!successfullyLockedByCurrentThread) {
                    log.info("Another instance is already refilling storage. Skipping...");
                    return;
                }

                log.info("Start refiling hash storage...");
                refillHashStorage();
            } catch (Exception e) {
                log.error("Error during storage refill", e);
            } finally {
                hashRepository.unlock(HASH_GENERATION_LOCK_ID);
            }
        });
    }
}