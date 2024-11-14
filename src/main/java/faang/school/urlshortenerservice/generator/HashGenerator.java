package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
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
    private final HashJdbcRepository hashJdbcRepository;
    private final Base62Encoder encoder;

    @Value("${generator.batch.size:100000}")
    private int batchSize;

    @Async("hashAsyncExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = encoder.encode(range);
        hashJdbcRepository.batchInsert(hashes);
    }

    @Async("hashAsyncExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);

        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(batchSize - hashes.size()));
        }

        return hashes.stream().map(Hash::getHash).toList();
    }
}
