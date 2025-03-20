package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.Hash.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.batch.size}")
    private int batchSize;

    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Transactional
    public CompletableFuture<List<String>> generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        if (numbers.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }
        return encodeAndSaveAsync(numbers);
    }

    @Async("saveHashExecutor")
    public CompletableFuture<List<String>> encodeAndSaveAsync(List<Long> numbers) {
        return encoder.encode(numbers).thenApply(hashes -> {
            hashRepository.save(hashes);
            return hashes;
        });
    }
}
