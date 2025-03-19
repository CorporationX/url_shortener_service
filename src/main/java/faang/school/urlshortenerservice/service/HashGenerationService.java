package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.repository.LockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerationService {
    private static final String GENERATION_LOCK = "hash_generation";

    private final HashGenerator hashGenerator;
    private final LockRepository lockRepository;
    private final HashCacheProperties properties;

    @Transactional
    public void generateHash(int currentCount) {
        if (isGenerationAvailable() && needsHashGeneration(currentCount)) {
            int neededHashes = calculateNeededHashes(currentCount);
            hashGenerator.generateAdditionalHashes(neededHashes);
            log.info("Generated {} new hashes, current count: {}", neededHashes, currentCount);
        }
    }

    private boolean isGenerationAvailable() {
        boolean lockAcquired = lockRepository.tryAcquireLock(GENERATION_LOCK);

        if (!lockAcquired) {
            log.debug("Generation lock is held by another instance");
        }

        return lockAcquired;
    }

    private boolean needsHashGeneration(int currentCount) {
        int minAllowed = getMinAllowedHashes();
        boolean needsGeneration = currentCount < minAllowed;

        if (needsGeneration) {
            log.debug("Hash generation needed. Current count: {}, minimum required: {}",
                    currentCount, minAllowed);
        } else {
            log.trace("Sufficient hashes available. Current count: {}, minimum required: {}",
                    currentCount, minAllowed);
        }

        return needsGeneration;
    }

    private int calculateNeededHashes(int currentCount) {
        return Math.min(
                getMaxAllowedHashes() - currentCount,
                properties.getMaxGenerationBatch()
        );
    }

    private int getMaxAllowedHashes(){
        return properties.getMaxDbMultiplier() * properties.getMaxSize();
    }

    private int getMinAllowedHashes(){
        return properties.getMinDbMultiplier() * properties.getMaxSize();
    }
}
