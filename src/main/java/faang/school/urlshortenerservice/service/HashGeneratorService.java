package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGeneratorService {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.batch-size}")
    private long batchSize;

    @Value("${hash.generator.lock-id}")
    private int lockId;

    @Transactional
    public void generateHash(long batchSize) {
        if (!hashRepository.acquireAdvisoryXactLock(lockId)) {
            log.debug("Lock not acquired. Skipping hash generation.");
            return;
        }

        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        if (numbers.isEmpty()) {
            throw new EntityNotFoundException("Failed to generate unique numbers for hashes.");
        }

        List<Hash> hashes = numbers.stream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
        log.info("Generated and saved {} hashes to pool", hashes.size());
    }

    @Transactional(readOnly = true)
    public List<String> getHashes(long batchSize) {
        List<String> hashes = hashRepository.getAndDeletedHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            log.error("HASH POOL UNDERFLOW: Requested {}, got only {}", batchSize, hashes.size());
            throw new IllegalStateException("Hash pool depleted. Refill was not triggered in time.");
        }
        return hashes;
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long batchSize) {
        return CompletableFuture.completedFuture(getHashes(batchSize));
    }
}