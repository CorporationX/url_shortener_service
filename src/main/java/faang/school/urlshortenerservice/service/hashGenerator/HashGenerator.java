package faang.school.urlshortenerservice.service.hashGenerator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.base62Encoder.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${unique_numbers.batchSize:1000}")
    private int batchSize;

    @Transactional
    public void generateAndSaveHashBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashList = base62Encoder.generateHashList(uniqueNumbers).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashList);
    }

    @Transactional
    public List<String> getHashes(long batchSize) {
        List<String> hashes = hashRepository.getHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            generateAndSaveHashBatch();
            hashes.addAll(hashRepository.getHashBatch(batchSize - hashes.size()));
        }
        return hashes;
    }

    @Async(value = "urlThreadPool")
    @Transactional
    public CompletableFuture<List<String>> getHashesAsync(long batchSize) {
        List<String> hashes = hashRepository.getHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            generateAndSaveHashBatch();
            hashes.addAll(hashRepository.getHashBatch(batchSize - hashes.size()));
        }
        return CompletableFuture.completedFuture(hashes);
    }
}
