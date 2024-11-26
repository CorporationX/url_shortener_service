package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.exeption.DataValidationException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final HashProperties hashProperties;

    @Transactional
    public List<Long> getUniqueNumbers(long n) {
        return hashRepository.getUniqueNumbers(n);
    }

    @Transactional
    public List<Hash> getHashBatch() {
        Integer batchSizeForGetHashes = hashProperties.getBatchSizeForGetHashes();
        return hashRepository.getHashBatch(batchSizeForGetHashes);
    }

    @Transactional
    public void saveAllHashes(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }

    @Transactional(readOnly = true)
    public int getCharLength() {
        return hashRepository.getCharLength();
    }

    @Retryable(
            retryFor = { DataValidationException.class },
            maxAttemptsExpression = "#{@hashProperties.getMaxAttempts()}",
            backoff = @Backoff(delayExpression = "#{@hashProperties.getDelay()}")
    )
    public Hash ensureHashExists(Hash hash) {
        return hashRepository.existsById(hash.getHash())
                ? hash
                : hashRepository.findUnusedHash()
                .map(unusedHash -> {
                    hashRepository.markHashAsReserved(unusedHash.getHash());
                    return unusedHash;
                })
                .orElseThrow(() -> new DataValidationException("Not found unused hash."));
    }
}
