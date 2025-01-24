package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${hash.generator.batchSize}")
    private int newHashBatchSize;
    @Value("${hash.generator.cashedHashesMinSize}")
    private int cashedHashesMinSize;

    @Async("generateHashesPool")
//    @Transactional
    public CompletableFuture<Void> generateBatch() {
        while (hashRepository.count() < cashedHashesMinSize) {
            log.info("generating new hashes, current count in DB: {}", hashRepository.count());
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(newHashBatchSize);
            List<Hash> newHashes = base62Encoder.encode(uniqueNumbers)
                    .stream()
                    .map(Hash::new)
                    .toList();

            log.info("saving generated hashes");
            hashRepository.saveAll(newHashes);
        }
        return CompletableFuture.completedFuture(null);
    }
}
