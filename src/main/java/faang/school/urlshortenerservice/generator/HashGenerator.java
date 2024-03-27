package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range}")
    private int batchSize;

    @Transactional
    public void generateHashes() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = base62Encoder.encode(numbers);
        hashRepository.saveAllAndFlush(hashes);
    }

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getAndDeleteHashBatch(amount);
        if (hashes.size() < amount) {
            generateHashes();
            hashes.addAll(hashRepository.getAndDeleteHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Async("executorService")
    @Transactional
    public CompletableFuture<List<Hash>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}