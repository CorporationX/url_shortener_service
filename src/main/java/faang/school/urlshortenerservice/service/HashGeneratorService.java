package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class HashGeneratorService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE = ALPHABET.length();
    private static final int HASH_GENERATION_LOCK_ID = 12345;
    private final HashRepository hashRepository;

    @Value("${hash.generator.batch-size:5000}")
    private long batchSize;

    @Transactional
    public void generateHash(long batchSize) {

        if (!hashRepository.acquireAdvisoryXactLock(HASH_GENERATION_LOCK_ID)) {
            return;
        }
        try {

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);

            if (uniqueNumbers.isEmpty()) {
                throw new EntityNotFoundException("NotFound uniqueNumbers for generate hashes");
            }

            List<Hash> hashes = uniqueNumbers
                    .stream()
                    .map(this::applyBase62Encoding)
                    .map(Hash::new)
                    .toList();
            hashRepository.saveAll(hashes);
        } finally {

        }
    }

    @Transactional
    public List<String> getHashes(long batchSize) {
        List<String> hashes = hashRepository.getAndDeletedHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            generateHash(batchSize);
            hashes.addAll(hashRepository.getAndDeletedHashBatch(batchSize - hashes.size()));
        }
        return hashes;
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long batchSize) {
        return CompletableFuture.completedFuture(getHashes(batchSize));
    }

    private String applyBase62Encoding(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(ALPHABET.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return stringBuilder.toString();
    }
}
