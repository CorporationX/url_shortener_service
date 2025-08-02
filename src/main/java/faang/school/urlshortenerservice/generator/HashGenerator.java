package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
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

    @Value("${url-shortener.hash.batch-size}")
    private int batchSize;

    @Async("hashGeneratorTaskExecutor")
    public void generateBatch() {
        log.info("Starting generation of {} hashes", batchSize);

        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            log.debug("Retrieved {} unique numbers from DB", uniqueNumbers.size());

            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            log.debug("Encoded {} numbers into unique hashes", hashes.size());

            hashRepository.save(hashes);
            log.info("Successfully generated and saved {} hashes to DB", hashes.size());
        } catch (Exception e) {
            log.error("Error while generating hash batch", e);
        }
    }
}
