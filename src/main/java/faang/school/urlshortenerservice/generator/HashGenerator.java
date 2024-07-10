package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Encoder;
import faang.school.urlshortenerservice.model.Hash;
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
    private final Encoder<Long> encoder;

    @Value("${hashGenerator.batch-size}")
    private int hashBatchSize;


    @Transactional
    public void generateHashBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(hashBatchSize);
        List<Hash> hashes = encoder.encode(numbers);
        hashRepository.saveAll(hashes);
        log.info("{} of hashes was generated", hashBatchSize);
    }

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateHashBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        log.info("{} of hashes were successfully received from database", amount);
        return hashes;
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Hash>> getHashesAsync(int amount) {
        return CompletableFuture.supplyAsync(() -> getHashes(amount));
    }
}