package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseConversion;
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

@RequiredArgsConstructor
@Slf4j
@Component
public class HashGenerator {

    private final HashRepository hashRepository;
    private final BaseConversion baseConversion;

    @Value("${app.hash.batch-size}")
    private int batchSize;

    @Transactional
    public void generateHashes() {
        log.info("Starting Hash generation with size batch: {}", batchSize);
        List<Long> uniqueNumbers = hashRepository.getFollowingRangeUniqueNumbers(batchSize);
        List<Hash> hashes = baseConversion.encode(uniqueNumbers).stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
        log.info("Hashes was successfully generated {}", hashes);
    }

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        log.info("Hashes in the amount of {} pieces were successfully obtained from the database", amount);
        if (hashes.size() < amount) {
            generateHashes();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        log.info("Received hashes from the database in the number {}", amount);
        return hashes;
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<Hash>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

}