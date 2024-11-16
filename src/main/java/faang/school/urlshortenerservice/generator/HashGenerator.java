package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${generator.hash.count}")
    private long countUniqueNumbers;

    @Async("asyncExecutor")
    public List<Hash> generateBatch(long countUniqueNumbers){
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(countUniqueNumbers);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers).stream().map(Hash::new).toList();
        return hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashBatch(int batchSize) {
        List<Hash> hashes = hashRepository.getHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            List<Hash> leftHashes = generateBatch(batchSize - hashes.size());
            log.info("Remaining hashes generated");
            hashes.addAll(leftHashes);
            log.info("Remaining hashes added to the general list");
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<String>> getHashBatchAsync(int batchSize) {
        return CompletableFuture.completedFuture(getHashBatch(batchSize));
    }
}
