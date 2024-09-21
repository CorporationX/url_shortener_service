package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${spring.hash.max_range}")
    private int maxRange;

    @Transactional
    public List<Hash> generateAndSaveBatches() {
        List<Hash> hashes = generateBatch(maxRange);
        return hashRepository.saveAll(hashes);
    }

    public List<Hash> generateBatch(int maxRange) {
        List<Long> numbers = hashRepository.getUniqueNumbers(maxRange);
        return base62Encoder.encode(numbers).stream()
                .map(Hash::new)
                .toList();
    }

    @Transactional
    public List<String> getHashBatch(int amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            List<Hash> extraHashes = generateBatch(amount - hashes.size());
            hashes.addAll(extraHashes);
        }

        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }

    @Async("taskExecutor")
    public CompletableFuture<List<String>> getHashBatchAsync(int amount) {
        return CompletableFuture.completedFuture(getHashBatch(amount));
    }
}
