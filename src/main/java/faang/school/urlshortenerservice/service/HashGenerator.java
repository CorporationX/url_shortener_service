package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepositoryJdbc;
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

    private final HashRepositoryJdbc hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.batch-size}")
    private int batchSize;

    //todo: parallelize hash generation
    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        try {
            log.info("Starting hash generation...");

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            hashRepository.save(hashes);

            log.info("Successfully generated and saved {} hashes.", hashes.size());
        } catch (Exception e) {
            log.error("Error generating hashes", e);
        }
    }

    public void syncGenerateBatch() {
        generateBatch();
    }
}
