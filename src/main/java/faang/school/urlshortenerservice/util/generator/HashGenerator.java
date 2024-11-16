package faang.school.urlshortenerservice.util.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {

    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Value("${hash.generator.batch-size}")
    private int batchSize;

    @Async("taskExecutor")
    public void generateBatch() {
        log.info("Start generate hashes");

        List<Long> sequence = hashRepository.getUniqueNumbers(batchSize);
        log.info("Generated sequence: {}", sequence.size());

        List<String> hashes = encoder.encodeBatch(sequence);
        hashRepository.save(hashes);
        log.info("Generated and saved {} hashes", hashes.size());
    }
}
