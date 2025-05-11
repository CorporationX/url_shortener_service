package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.shortener.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.batch.size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        try {
            List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
            log.info("Generated {} unique numbers", numbers.size());

            List<String> hashes = base62Encoder.encode(numbers);
            log.info("Encoded to {} hashes", hashes.size());

            hashRepository.saveHashes(hashes);
            log.info("Saved {} hashes to DB", hashes.size());

        } catch (Exception e) {
            log.error("Error during hash batch generation", e);
        }
    }
}
