package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.max_range}")
    private int maxRange;

    @Async("taskExecutor")
    @Scheduled(cron = "${hash.generator.cron}")
    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashBatch = base62Encoder.encode(numbers).stream().map(Hash::new).toList();
        hashRepository.saveAll(hashBatch);
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<String>> getHashAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
