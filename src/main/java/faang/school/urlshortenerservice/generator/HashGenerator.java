package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseEncoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Value("${hash.generator.batch-size:1000}")
    private int batchSize;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateHash() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        if (uniqueNumbers.isEmpty()) {
            throw new EntityNotFoundException("Не удалось получить уникальные числа для генерации хэшей");
        }
        List<String> hashes = baseEncoder.encode(uniqueNumbers);

        hashRepository.saveHashBatch(hashes);
    }

    @Transactional
    public List<Hash> getHashes() {
        List<Hash> hashes = hashRepository.getAndDeleteHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            generateHash();
            hashes.addAll(hashRepository.getAndDeleteHashBatch(batchSize - hashes.size()));
        }
        return hashes;
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<Hash>> getHashesAsync() {
        return CompletableFuture.supplyAsync(this::getHashes);
    }
}
