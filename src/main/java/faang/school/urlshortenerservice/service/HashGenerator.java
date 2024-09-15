package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash.repository.batchSize}")
    private long repositoryBatchSize;

    @Async("hashGeneratorThreadPool")
    public CompletableFuture<List<String>> generateHashBatchAsync(int size) {
        return CompletableFuture.completedFuture(generateHashBatch(size));
    }

    public List<String> generateHashBatch(int size) {
        generateToRepository();
        return hashRepository.getHashesAndDelete(size);
    }

    private void generateToRepository() {
        Long actualHashCount = this.hashRepository.getActualHashCount();
        int batchSize = Math.toIntExact(this.repositoryBatchSize - actualHashCount);
        if (batchSize > 0) {
            List<Long> uniqNums = this.hashRepository.getUniqueNumbers(batchSize);
            List<String> hashes = this.base62Encoder.encode(uniqNums);
            this.hashRepository.saveHashes(hashes);
            log.info("Generated hashes to repository, oldCount: {} addedCount: {}", actualHashCount, batchSize);
        }
    }
}
