package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.config.HashConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private final HashConfig hashConfig;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final HashStorageAsyncFiller hashStorageAsyncFiller;

    @Transactional
    public List<String> getFreeHashes(long count) {
        List<String> freeHashes = hashRepository.getFreeHashBatchWithLockAndDelete(count);
        long currentRepositoryCount = hashRepository.count();
        log.debug("Left free hashes - {}", currentRepositoryCount);

        boolean lessThanNeeded = freeHashes.size() < count;
        if (lessThanNeeded) {
            int missingCount = (int) count - freeHashes.size();
            log.warn("Not enough free hashes for cache. Generate missing {} directly", missingCount);
            List<String> missingHashes = hashGenerator.generateHashes(missingCount);
            freeHashes.addAll(missingHashes);
        }
        boolean needRefill = lessThanNeeded || currentRepositoryCount < hashConfig.getStorageUpdateCount();

        if (needRefill) {
            log.warn("Start refilling hash repository. Current repository size - {}, limit - {}",
                    currentRepositoryCount,
                    hashConfig.getStorageUpdateCount());

            hashStorageAsyncFiller.refillStorageAsync(hashConfig.getStorage().getSize());
        }
        return freeHashes;
    }
}