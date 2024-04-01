package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
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

    @Value("${hash.range:10000}")
    private int maxRange;

    @Transactional
    @Scheduled(cron = "${scheduled.cron}")
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = range.stream()
                .map(base62Encoder::base62Encoding)
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<Hash> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes;
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Hash>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
