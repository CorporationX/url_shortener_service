package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Value("${generator.batch.size}")
    private int batchSize;

    @Value("${cache.min_percent_hashes}")
    private double minPercentHashes;

    @Transactional
    private List<String> generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = encoder.encode(range);
        executor.execute(() -> {
            List<Hash> hashesEntity = encoder.encode(range).stream()
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(hashesEntity);
        });

        return hashes;
    }

    @Transactional
    public List<String> getHashes(int amount) {
        Long hashesSize = hashRepository.getHashesSize();
        double cacheFullPercentage = 100.0 / batchSize * hashesSize;

        if (cacheFullPercentage <= minPercentHashes) {
            return generateBatch().subList(0, amount);
        }

        return hashRepository.getHashBatch(amount);
    }
}
