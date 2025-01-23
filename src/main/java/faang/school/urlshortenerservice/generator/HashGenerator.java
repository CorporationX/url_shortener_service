package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.max-range}")
    private long maxRange;

    @Transactional
    @Scheduled(cron = "${hash.cron:0 0 0 * * *}")
    public void generateHash() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = base62Encoder.encode(range)
                .stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }

    @Async("taskExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
