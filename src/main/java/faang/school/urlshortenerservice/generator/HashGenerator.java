package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
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
    private final Base62Encoder base62Encoder;

    @Value("${spring.hash.generation-batch-size:100}")
    private int generationBatchSize;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Void> generateBatch() {
        try {
            List<Long> numbers = hashRepository.getUniqueNumbers(generationBatchSize);
            List<String> hashes = base62Encoder.encode(numbers);
            hashRepository.save(hashes);

            log.info("Generated and saved {} hashes", hashes.size());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error generating hash batch", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
