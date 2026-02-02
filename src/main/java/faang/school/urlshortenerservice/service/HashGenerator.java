package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueIdRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final UniqueIdRepository uniqueIdRepository;
    private final Base62Encoder encoder;

    @Value("${hash.generator.batch-size}")
    private int batchSize;

    // used during normal operation to fill
    // hash storage without blocking
    @Async("hashGeneratorExecutor")
    public void generateBatchAsync() {
        generateBatch();
    }

    // used only during application startup to guarantee
    // that hash storage is not empty before accepting traffic
    public void generateBatchSyncForBootstrap() {
        generateBatch();
    }

    private void generateBatch() {
        List<Long> uniqueNumbers = uniqueIdRepository.getNextIds(batchSize);
        List<String> generatedHashes = encoder.encode(uniqueNumbers);
        hashRepository.saveHashes(generatedHashes);
    }
}