package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.BatchSizeException;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final EntityManager entityManager;

    @Value("${spring.cache.capacity}")
    private int cacheCapacity;

    @Value("${spring.hash.max-range}")
    private int maxRangeNumbers;

    @Value("${spring.hash.batch-size}")
    private int batchSize;

    public void generateBatchHashes() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRangeNumbers);
        List<Hash> hashList = base62Encoder.encode(uniqueNumbers)
                .stream()
                .map(s -> Hash.builder().hash(s).build())
                .toList();

        hashRepository.saveAllBatch(hashList, entityManager, batchSize);
    }

    @Transactional
    public List<String> getHashes() {
        if (batchSize > cacheCapacity) {
            throw new BatchSizeException("The batch size can't be more than the cache capacity");
        }

        List<Hash> hashes = hashRepository.getAndDelete(batchSize);

        if (hashes.size() < batchSize) {
            generateBatchHashes();
            hashes.addAll(hashRepository.getAndDelete(batchSize - hashes.size()));
        }
        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }
}
