package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final int batchSize;

    public HashGenerator(HashRepository hashRepository,
                         Base62Encoder base62Encoder,
                         @Value("${url.hash.batch-size:1000}") int batchSize) {
        this.hashRepository = hashRepository;
        this.base62Encoder = base62Encoder;
        this.batchSize = batchSize;
    }

    @Transactional
    @Async("hashGeneratorThreadPool")
    public void generateBatch() {
        try {
            log.info("Starting hash generation batch...");
            var numbers = hashRepository.getUniqueNumbers(batchSize);
            var hashes = base62Encoder.encode(numbers);
            hashRepository.save(hashes);
            log.info("Finished generating and saving {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Error in generateBatch", e);
            throw e;
        }
    }

}
