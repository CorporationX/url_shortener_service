package faang.school.urlshortenerservice.hash_generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;

    @Value("${hash.generator.batch.size}")
    private int batchSize;

    @PostConstruct
    public void init() {
        if (hashRepository.getHashCount() == 0) {
            generateBatch();
        }
    }

    @Async("taskExecutor")
    public void generateBatchAsync() {
        generateBatch();
    }

    private void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveBatch(hashes);
        log.info("Generated {} unique hashes", hashes.size());
    }
}
