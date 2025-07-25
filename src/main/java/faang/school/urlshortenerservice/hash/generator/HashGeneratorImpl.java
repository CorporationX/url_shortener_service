package faang.school.urlshortenerservice.hash.generator;

import faang.school.urlshortenerservice.config.hash.HashProperties;
import faang.school.urlshortenerservice.hash.Base62Encoder;
import faang.school.urlshortenerservice.hash.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final HashProperties hashProperties;

    @PostConstruct
    private void postConstruct() {
        generateBatch();
    }

    @Async("hashGeneratorPool")
    @Override
    public void generateBatch() {
        log.info("Hashes generation started.");

        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashProperties.generatedCount());
        List<String> hashes = encoder.encodeBatch(uniqueNumbers);
        hashRepository.saveBatch(hashes);

        log.info("Hashes generated. From {} to {}", uniqueNumbers.get(0), uniqueNumbers.get(uniqueNumbers.size() - 1));
    }
}
