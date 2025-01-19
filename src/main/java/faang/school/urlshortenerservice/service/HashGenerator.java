package faang.school.urlshortenerservice.service;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
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
            int n = batchSize;
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);
            log.info("Fetched {} unique numbers.", uniqueNumbers.size());

            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            log.info("Generated {} hashes.", hashes.size());

            hashRepository.save(hashes);
        } catch (Exception e) {
            log.error("Error generating hash batch", e);
        }
    }
}
