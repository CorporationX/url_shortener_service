package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
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

    @Value("${hashGenerator.batchSize}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    public void generateHashes() {
        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            log.info("Fetched {} unique numbers for hashing.", uniqueNumbers.size());

            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            log.info("Generated {} hashes.", hashes.size());

            hashRepository.save(hashes);
        } catch (Exception e) {
            log.error("Error generating hash batch", e);
        }
    }
}
