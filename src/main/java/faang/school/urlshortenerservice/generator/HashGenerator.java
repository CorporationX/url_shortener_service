package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.managers.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.max-range}")
    private int maxRange;

    @Value("${hash.generator.butch-size:10}")
    private int hashButhSize;

    @Transactional
    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        log.info("Generating hash batch");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRange);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveBatch(hashes);
    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<Hash>> getHashBatch() {
        log.info("Getting hash batch asynchronously");
        List<Hash> hashes = hashRepository.getHashBatch(hashButhSize);
        if (hashes.size() < hashButhSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(hashButhSize));
        }
        return CompletableFuture.completedFuture(hashes);
    }

    @Transactional
    public List<Hash> getHashBatchSync() {
        log.info("Getting hash batch synchronously");
        List<Hash> hashes = hashRepository.getHashBatch(hashButhSize);
        return hashes;
    }
}
