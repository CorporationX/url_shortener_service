package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Value("${hash.generator.batch-size}")
    private long maxRange;

    @Transactional
    @Scheduled(cron = "${hash.scheduled.cron}")
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        log.info("Generating Base62 hashes for range: {}", range);
        List<Hash> hashes = base62Encoder.encoder(range).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashBatch(long amount) {
        List<String> hashes = hashRepository.getHashBatch(amount);
        log.info("Retrieved {} hashes from the repository: {}", hashes.size(), hashes);
        if (hashes.size() < amount) {
            log.info("Not enough hashes, generating more.");
            generateBatch();
            hashes.addAll(getHashBatch(amount - hashes.size()));
        }
        log.info("Final hash batch size: {}", hashes.size());
        return hashes;
    }

    @Async("hashGeneratorTaskExecutor")
    public CompletableFuture<List<String>> getHashBatchAsync(long amount) {
        return CompletableFuture.completedFuture(getHashBatch(amount));
    }
}

