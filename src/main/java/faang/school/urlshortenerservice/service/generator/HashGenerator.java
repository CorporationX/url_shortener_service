package faang.school.urlshortenerservice.service.generator;

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

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.batch_size}")
    private int batchSize;

    @Transactional
    public List<String> generateHashes(long amount) {
        List<Long> range = hashRepository.getUniqueNumbers(amount);
        return base62Encoder.encode(range);
    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public void generateHashesAsync() {
        List<String> hashes = generateHashes(batchSize);
        List<Hash> batch = hashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(batch);
        log.info("{} hashes generated", batchSize);
    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<String> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            long difference = amount - hashes.size();
            List<String> newHashes = generateHashes(difference);
            hashes.addAll(newHashes);
            log.info("Local cache is full. Start generating new hashes for DB");
            generateHashesAsync();
        }
        return hashes;
    }
}
