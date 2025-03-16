package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final HashService hashService;
    private final Base62Encoder base62Encoder;
    private final ThreadPoolHashing threadPoolHashing;
    private ExecutorService executor;

    @Value("${hash.generator.batch-size}")
    private int batchFromFile;

    @PostConstruct
    public void init() {
        this.executor = threadPoolHashing.createExecutorService();
    }

    @Async
    public CompletableFuture<List<String>> generateBatch() {

        return CompletableFuture.supplyAsync(
                () -> {
                    List<Long> batches = hashRepository.getUniqueNumbers(batchFromFile);
                    List<String> resultOfEncoder = base62Encoder.encode(batches);
                    hashService.saveHashes(resultOfEncoder);
                    return resultOfEncoder;
                },
                executor
        );
    }
}
