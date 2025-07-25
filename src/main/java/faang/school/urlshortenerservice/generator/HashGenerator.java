package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.FreeHashPool;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value(value = "${spring.hash.range}")
    private int maxRange;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void generateHash() {
        log.info("Generating hash pool...");
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<FreeHashPool> hashes = range.stream()
                .map(base62Encoder::encode)
                .map(hashValue -> FreeHashPool.builder().hash(hashValue).build())
                .toList();
        hashRepository.saveAll(hashes);
        log.info("Hash pool generated.");
    }

    @Transactional
    public List<String> getHashes(int amount) {
        log.info("Getting {} hashes from hash pool...", amount);
        List<String> hashes = hashRepository.getHashBatchAndDelete(amount);
        if (hashes.size() < amount) {
            log.info("Not enough hashes in hash pool. Generating more...");
            generateHash();
            hashes.addAll(hashRepository.getHashBatchAndDelete(amount - hashes.size()));
        }
        log.info("Got {} hashes from hash pool.", hashes.size());
        return hashes;
    }

    @Async("threadPoolTaskExecutor")
    @Transactional
    public CompletableFuture<List<String>> getHashesAsync(int amount) {
        log.info("Getting {} hashes from hash pool asynchronously...", amount);
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
