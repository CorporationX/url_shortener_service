package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.LockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerationService {
    private final LockRepository lockRepository;
    private final HashCacheProperties properties;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${spring.hash-cache.lock-name}")
    private String lockName;

    public boolean needsHashGeneration(int currentCount) {
        int minAllowed = properties.getMinDbMultiplier() * properties.getMaxSize();
        return currentCount < minAllowed;
    }

    @Transactional
    public void generateHash(int currentCount) {
        if (!lockRepository.tryAcquireLock(lockName)) {
            log.debug("Lock {} is already acquired", lockName);
            return;
        }

            int neededHashes = calculateNeededHashes(currentCount);
            generateAdditionalHashes(neededHashes);
    }

    private int calculateNeededHashes(int currentCount) {
        int maxAllowedHashes = properties.getMaxDbMultiplier() * properties.getMaxSize();
        return maxAllowedHashes - currentCount;
    }

    public void generateAdditionalHashes(int count) {
        List<Long> numbers = hashRepository.getNextSequenceValues(count);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.saveHashes(hashes);
    }
}
