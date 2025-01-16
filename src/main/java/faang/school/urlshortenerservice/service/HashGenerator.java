package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.batch-size}")
    private int batchSize;

    @Value("${hash.min-percent-hashes}")
    private double minPercentHashes;

    @Transactional
    public List<String> getHashes(int amount) {
        double sizeHashCache = hashRepository.getHashesSize();
        double cacheFullnessPercentage = sizeHashCache / batchSize * 100.0;

        if (cacheFullnessPercentage <= minPercentHashes) {
            return generateBatch().subList(0, amount);
        }

        return hashRepository.getHashBatch(amount);
    }

    @Async("ThreadAsyncExecutor")
    protected List<String> generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(numbers);
        List<Hash> hashesEntity = hashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashesEntity);
        return hashes;
    }
}