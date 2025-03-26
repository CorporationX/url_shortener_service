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
    private final HashRepository hashRepository;

    @Value("${encoder.alphabet}")
    private String alphabet;

    @Value("${encoder.base}")
    private int base;
    @Value("${hash.generator.batch-size:1000}")
    private long batchSize;

    @Transactional
    public void generateHash(long batchSize) {

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

    String applyBase62Encoding(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(alphabet.charAt((int) (number % base)));
            number /= base;
        }
        return stringBuilder.toString();
    }
}
