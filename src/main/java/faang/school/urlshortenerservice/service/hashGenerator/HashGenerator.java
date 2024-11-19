package faang.school.urlshortenerservice.service.hashGenerator;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.base62Encoder.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashConfig hashConfig;

    @Transactional
    public void generateAndSaveHashBatch() {
        log.info("Generating and saving hash batch of size {}", hashConfig.getBatchSizeUniqueNumbers());
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashConfig.getBatchSizeUniqueNumbers());
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
