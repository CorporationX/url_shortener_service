package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
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

    @Value("${app.hash-generator.batch-size:5000}")
    private int batchSize;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @PostConstruct
    @Transactional
    public void init() {
        generateBatchIfNeeded();
    }

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateBatchIfNeeded() {
        if (hashRepository.getHashesNumber() < batchSize) {
            log.info("Hashes number is under {}, refilling DB", batchSize);

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            hashRepository.saveBatch(hashes);
        }
    }
}
