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

    @Value("${hash.generator.batch-size:100000}")
    private int batchSize;

    /**
     * Асинхронно генерирует и сохраняет пачку хэшей в БД
     *
     * @return CompletableFuture с количеством сгенерированных хэшей
     */
    @Async("hashGeneratorExecutor")
    public CompletableFuture<Integer> generateBatch() {
        log.info("Starting hash generation batch of size {}", batchSize);

        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            log.debug("Retrieved {} unique numbers from database", uniqueNumbers.size());

            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            log.debug("Encoded {} numbers to Base62 hashes", hashes.size());

            hashRepository.save(hashes);
            log.info("Successfully generated and saved {} hashes to database", hashes.size());

            return CompletableFuture.completedFuture(hashes.size());
        } catch (Exception e) {
            log.error("Error during hash generation batch", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}