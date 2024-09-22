package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorThreadPool")
    public CompletableFuture<List<String>> generateBatchAsync(int numberOfBatch) {
        return CompletableFuture.completedFuture(generateBatch(numberOfBatch));
    }

    public List<String> generateBatch(int numberOfBatch) {
        List<Long> uniqueNumber = hashRepository.getUniqueNumbers(numberOfBatch);
        List<String> hashes = base62Encoder.encode(uniqueNumber);
        log.info("Create batch hashes. Batch size: {}", numberOfBatch);
        hashRepository.save(hashes);
        return hashes;
    }
}
