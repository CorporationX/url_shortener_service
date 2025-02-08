package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.managers.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashJdbcRepository hashJdbcRepository;
    private final Base62Encoder base62Encoder;

    @Value("${spring.url.hash.generator.max-range}")
    private int maxRange;

    @Value("${spring.url.hash.generator.hash-butch-size}")
    private int hashButchSize;

    @Async("hashGeneratorExecutor")
    @Transactional
    @Scheduled(cron = "${spring.url.hash.generator.cron}")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRange);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashJdbcRepository.saveBatch(hashes);
    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<Hash>> getHashes() {
        List<Hash> hashes = hashRepository.getHashBatch(hashButchSize);
        if (hashes.size() < hashButchSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(hashButchSize));
        }
        return CompletableFuture.completedFuture(hashes);
    }

    @Transactional
    public List<Hash> getHashesSync() {
        List<Hash> hashes = hashRepository.getHashBatch(hashButchSize);
        if (hashes.size() < hashButchSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(hashButchSize));
        }
        return hashes;
    }
}
