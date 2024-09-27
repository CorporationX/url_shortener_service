package faang.school.urlshortenerservice.generator;

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
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.batch_size.generate}")
    private int generateBatchSize;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Void> generateBatchAsync() {
        generateBatch();
        return CompletableFuture.completedFuture(null);
    }

    @PostConstruct
    public void generateBatch() {
        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(generateBatchSize);
            List<String> hashes = encoder.encode(uniqueNumbers);
            hashRepository.save(hashes);
        } catch (Exception e) {
            log.error("Exception in HashGenerator", e);
        }
    }
}