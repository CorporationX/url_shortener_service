package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash-generator.max-range}")
    private int maxRange;

    @Value("${hash-generator.batch-size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateBatch() {
        log.info("Generating hash batch");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = base62Encoder.encodeBatch(uniqueNumbers);
        hashRepository.saveBatch(hashes);
    }

    @Async("hashGeneratorExecutor")
    @Transactional
    public CompletableFuture<List<Hash>> getHashBatch() {
        List<Hash> hashes = hashRepository.getHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(batchSize));
        }
        return CompletableFuture.completedFuture(hashes);
    }

    @Transactional
    public List<Hash> getHashBatchSync() {
        return hashRepository.getHashBatch(batchSize);
    }
}
