package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final int batchSize;

    public HashGenerator(HashRepository hashRepository,
                         @Value("${hash.batch_size}") int batchSize) {
        this.hashRepository = hashRepository;
        this.batchSize = batchSize;
    }

    @Async
    @Transactional
    public void generateBatch() {
        log.info("Generating {} hashes", batchSize);
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = Base62Encoder.encode(numbers).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        log.info("Got {} hashes from DB", hashes.size());
        if (hashes.size() < amount) {
            generateBatch();
            log.info("Get {} hashes from DB", amount - hashes.size());
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Async
    @Transactional
    public CompletableFuture<List<Hash>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
