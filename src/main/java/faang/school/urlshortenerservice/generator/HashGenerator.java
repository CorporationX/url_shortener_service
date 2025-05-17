package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.shortener.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashProperties hashProperties;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        try {
            List<Long> numbers = hashRepository.getUniqueNumbers(hashProperties.getGenerator().getBatchSize());

            List<String> hashes = base62Encoder.encode(numbers);

            hashRepository.saveHashes(hashes);
            log.info("Generated and saved {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Error during hash batch generation", e);
        }
    }
}
