package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash-generator.count-generate-hashes}")
    private int countGenerateHashes;

    @Value("${hash-generator.min-free-ratio-hashes}")
    private double minFreeRatio;

    @PostConstruct
    public void init() {
        log.info("Initializing HashGenerator");
        checkAndGenerateHashesAsync();
    }

    @Async("hashGeneratorExecutor")
    public void checkAndGenerateHashesAsync() {
        log.debug("Checking and generating hashes asynchronously");
        checkAndGenerateHashes();
    }

    private void checkAndGenerateHashes() {
        long hashesCount = hashRepository.getCountOfHashes();
        if (hashesCount / (double) countGenerateHashes < minFreeRatio) {
            generateHashes(countGenerateHashes);
        }
    }

    private void generateHashes(int count) {
        log.info("Generating {} hashes", count);
        List<Long> numbers = hashRepository.getUniqueNumbers(count);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.saveHashes(hashes);
        log.info("Generated and saved {} hashes", hashes.size());
    }
}