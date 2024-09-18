package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public List<Hash> generateBatch(long amount) {
        List<Long> range = hashRepository.getUniqueNumbers(amount);
        log.info("Generating {} Base62 hashes for range: {}", amount, range);
        return base62Encoder.encoder(range).stream()
                .map(Hash::new)
                .toList();
    }

    @Transactional
    public void generateAndSaveBatch(long amount) {
        List<Hash> hashes = generateBatch(amount);
        hashRepository.saveAll(hashes);
        log.info("Saved {} hashes to the database.", hashes.size());
    }

    @Transactional
    public List<String> getHashBatch(long amount) {
        List<String> hashes = hashRepository.getHashBatch(amount);
        log.info("Retrieved {} hashes from the repository: {}", hashes.size(), hashes);
        if (hashes.size() < amount) {
            log.info("Not enough hashes, generating more.");
            List<Hash> generatedHashes = generateBatch(amount - hashes.size());
            hashRepository.saveAll(generatedHashes);
            hashes.addAll(generatedHashes.stream().map(Hash::getHash).toList());
        }
        log.info("Final hash batch size: {}", hashes.size());
        return hashes;
    }

    @Async("hashGeneratorTaskExecutor")
    public CompletableFuture<List<String>> getHashBatchAsync(long amount) {
        return CompletableFuture.completedFuture(getHashBatch(amount));
    }
}

