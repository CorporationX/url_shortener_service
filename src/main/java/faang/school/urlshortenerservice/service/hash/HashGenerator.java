package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashService hashService;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash.generation-batch-size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateBatch() {
        log.info("Starting hash batch generation with size: {}", batchSize);
        
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        log.debug("Generated {} unique numbers", uniqueNumbers.size());

        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        log.debug("Encoded {} hashes", hashes.size());

        hashService.saveHashes(hashes);
        log.info("Successfully saved {} hashes to database", hashes.size());
    }
}
