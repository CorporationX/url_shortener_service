package faang.school.url_shortener_service.generator;

import faang.school.url_shortener_service.entity.Hash;
import faang.school.url_shortener_service.repository.hash.HashRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.batch}")
    private int hashBatchSize;

    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashBatchSize);
        List<Hash> hashList = base62Encoder.encode(uniqueNumbers);
        log.info("Hash generator generated {} hashes", hashList.size());
        hashRepository.saveAll(hashList);
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            log.info("No hashes in DB - generating...");
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
            log.info("Pulled {} hashes after generation", hashes.size());
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("hashGenerationExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<List<String>> generateHashesAsync(int amount) {
        String threadName = Thread.currentThread().getName();
        log.info("Generating hashes in thread {}", threadName);
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional
    public void saveHashesToDb(List<String> hashes) {
        List<Hash> hashEntities = hashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashEntities);
    }
}