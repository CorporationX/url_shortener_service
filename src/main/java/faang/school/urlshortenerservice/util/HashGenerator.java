package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
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

    @Value("${hash.batch-size}")
    private int batchSize;

    @Async("hashGeneratorThreadPool")
    public void generateBatch() {
        log.info("Start generating hash batch of size {}", batchSize);

        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = Base62Encoder.encode(uniqueNumbers);
        hashRepository.saveAllHashes(hashes);

        log.info("Generated and saved {} hashes successfully", hashes.size());
    }
}