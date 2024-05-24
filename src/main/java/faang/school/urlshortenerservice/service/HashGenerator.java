package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    @Value("${hash.batch-size}")
    private int batchSize;
    private final Base62Encoder base62Encoder;


    @PostConstruct
    public void populate() {
        generateBatch(batchSize);
    }


    @Async("hashExecutor")
    public CompletableFuture<List<Hash>> generateBatch(int currentBatchSize) {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers).stream()
                .map(Hash::new)
                .toList();
        return CompletableFuture.completedFuture(hashRepository.saveAll(hashes));
    }

    @Async("hashExecutor")
    public CompletableFuture<List<Hash>> generateBatchAsync(int batchSize) {
        return generateBatch(batchSize);
    }
}
