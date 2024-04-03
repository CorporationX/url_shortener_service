package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueSequenceIdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Bulgakov
 */
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final Base62Encoder base62Encoder;
    private final UniqueSequenceIdRepository uniqueSequenceIdRepository;
    private final HashRepository hashRepository;
    @Value("${hash.range:1000}")
    private int maxRange;

    @Transactional
    public List<String> getHash(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            hashes.addAll(generateBatch());
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<String>> getHashAsync(long amount) {
        return CompletableFuture.completedFuture(getHash(amount));
    }

    @Transactional
    public List<Hash> generateBatch() {
        List<Long> range = uniqueSequenceIdRepository.getNextRange(maxRange);
        List<Hash> hashes = base62Encoder.encode(range).stream()
                .map(Hash::new)
                .toList();

        return hashRepository.saveAll(hashes);
    }
}
